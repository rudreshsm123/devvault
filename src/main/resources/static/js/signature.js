/* Splits the text of every [data-signature] element into letters so signature.css can animate them. */
(function () {
    'use strict';

    function split(el) {
        var text = el.textContent;
        if (!text || el.dataset.ready) {
            return;
        }
        el.setAttribute('aria-label', text);            // screen readers read the name once, not letter by letter
        el.textContent = '';
        Array.prototype.forEach.call(text, function (ch, i) {
            var span = document.createElement('span');
            span.className = 'ch';
            span.setAttribute('aria-hidden', 'true');
            span.style.setProperty('--i', i);
            span.textContent = ch === ' ' ? ' ' : ch;
            el.appendChild(span);
        });
        el.dataset.ready = 'true';
    }

    function init() {
        try {
            document.querySelectorAll('[data-signature]').forEach(split);
        } catch (e) {
            // Decorative only.
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
