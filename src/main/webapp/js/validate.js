/**
 * validate.js — JSON-driven client-side form validation.
 *
 * How it works:
 * 1. On DOMContentLoaded, fetch /resources/validation/messages.json
 * 2. Cache the rules object.
 * 3. Find all <form data-validate-form="..."> elements.
 * 4. On submit, look up the form's rules from the cached JSON.
 * 5. Validate each field against its defined rules.
 * 6. On failure: show inline Bootstrap-style error messages, prevent submission.
 * 7. On pass: allow the form to submit normally.
 *
 * Supported rule types (mirroring server-side ValidationUtil):
 *   required   — field must not be blank
 *   minLength  — value.length >= minLength.value
 *   email      — regex email format check
 *   pattern    — custom regex (pattern.value)
 *   matchField — value must equal another field's value (matchField.field)
 *   positive   — must be a number > 0
 *   nonNegative — must be a number >= 0
 *
 * Usage in JSP:
 *   <form data-validate-form="registerForm" ...>
 *     <input name="email" ...>
 *   </form>
 */

// contextPath is set as a global by the JSP header:
//   <script>const contextPath = '<%=request.getContextPath()%>';</script>

let validationRules = {};

document.addEventListener('DOMContentLoaded', function () {
    // Fetch validation rules from JSON file
    fetch(contextPath + '/resources/validation/messages.json')
        .then(function (res) { return res.json(); })
        .then(function (data) {
            validationRules = data.forms || {};
            setupFormValidation();
        })
        .catch(function (err) {
            console.warn('[ApolloCare] Could not load validation rules:', err);
            // App still works — server-side validation is the authoritative check
        });
});

function setupFormValidation() {
    var forms = document.querySelectorAll('form[data-validate-form]');
    forms.forEach(function (form) {
        form.addEventListener('submit', function (event) {
            var formKey = form.getAttribute('data-validate-form');
            var rules   = validationRules[formKey];
            if (!rules) return;  // No rules defined for this form — allow submit

            var isValid = validateForm(form, rules);
            if (!isValid) {
                event.preventDefault();  // Stop form submission
            }
        });
    });
}

/**
 * Validates all fields in a form against the provided rules.
 * Returns true if all fields are valid.
 */
function validateForm(form, rules) {
    // Clear previous errors
    form.querySelectorAll('.validation-error').forEach(function (el) { el.remove(); });
    form.querySelectorAll('.is-invalid').forEach(function (el) { el.classList.remove('is-invalid'); });

    var isValid = true;

    for (var fieldName in rules) {
        if (!rules.hasOwnProperty(fieldName)) continue;

        var field = form.querySelector('[name="' + fieldName + '"]');
        if (!field) continue;

        var value      = field.value.trim();
        var fieldRules = rules[fieldName];
        var error      = applyRules(form, field, value, fieldRules);

        if (error) {
            showError(field, error);
            isValid = false;
        }
    }

    return isValid;
}

/**
 * Applies all rules for a single field. Returns the first error message, or null.
 */
function applyRules(form, field, value, rules) {

    // required
    if (rules.required) {
        var isEmpty = value === '' || (field.tagName === 'SELECT' && value === '');
        if (isEmpty) return rules.required;
    }

    // Skip remaining rules if field is blank (required would have caught it)
    if (value === '') return null;

    // minLength
    if (rules.minLength) {
        if (value.length < rules.minLength.value) return rules.minLength.message;
    }

    // email format
    if (rules.email) {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
        if (!emailRegex.test(value)) return rules.email;
    }

    // pattern (custom regex)
    if (rules.pattern) {
        var re = new RegExp(rules.pattern.value);
        if (!re.test(value)) return rules.pattern.message;
    }

    // matchField (e.g. confirmPassword must match password)
    if (rules.matchField) {
        var otherField = form.querySelector('[name="' + rules.matchField.field + '"]');
        if (otherField && value !== otherField.value.trim()) {
            return rules.matchField.message;
        }
    }

    // positive (number > 0)
    if (rules.positive) {
        var num = parseFloat(value);
        if (isNaN(num) || num <= 0) return rules.positive;
    }

    // nonNegative (number >= 0)
    if (rules.nonNegative) {
        var n = parseFloat(value);
        if (isNaN(n) || n < 0) return rules.nonNegative;
    }

    return null;  // No error
}

/**
 * Adds the Bootstrap is-invalid class and inserts an inline error message.
 */
function showError(field, message) {
    field.classList.add('is-invalid');

    var errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback validation-error';
    errorDiv.textContent = message;

    // Insert after the field element
    field.parentNode.insertBefore(errorDiv, field.nextSibling);
}
