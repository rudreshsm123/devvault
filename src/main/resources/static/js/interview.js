/* Timed mock interview: counts down from the time the SERVER says is left, collects the answers and submits them
   when the learner presses Finish or the time runs out. The server grades; no answer is ever on this page. */
(function () {
    'use strict';

    var root = document.getElementById('interview');
    if (!root) {
        return;
    }
    var timer = document.getElementById('timer');
    var finishButton = document.getElementById('finish-btn');
    var message = document.getElementById('interview-msg');
    var csrfToken = (document.querySelector('meta[name="_csrf"]') || {}).content;
    var csrfHeader = (document.querySelector('meta[name="_csrf_header"]') || {}).content;

    var remaining = parseInt(root.dataset.remaining, 10) || 0;
    var started = performance.now();               // a monotonic clock, so changing the computer's time does nothing
    var submitting = false;

    function format(seconds) {
        var m = Math.floor(seconds / 60);
        var s = seconds % 60;
        return (m < 10 ? '0' : '') + m + ':' + (s < 10 ? '0' : '') + s;
    }

    function secondsLeft() {
        return Math.max(0, remaining - Math.floor((performance.now() - started) / 1000));
    }

    function collect() {
        var answers = {};
        root.querySelectorAll('.iq').forEach(function (item) {
            var radio = item.querySelector('input[type="radio"]:checked');
            var input = item.querySelector('.iq-input');
            var value = radio ? radio.value : (input ? input.value : '');
            if (value && value.trim()) {
                answers[item.dataset.drill] = value.trim();
            }
        });
        return answers;
    }

    function finish(reason) {
        if (submitting) {
            return;
        }
        submitting = true;
        finishButton.disabled = true;
        message.textContent = reason === 'time' ? 'Time is up. Checking your answers...' : 'Checking your answers...';
        var headers = { 'Content-Type': 'application/json' };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        fetch('/api/me/interview/' + root.dataset.id + '/finish', {
            method: 'POST', headers: headers, credentials: 'same-origin',
            body: JSON.stringify({ answers: collect() })
        }).then(function (r) {
            if (!r.ok) { throw new Error('HTTP ' + r.status); }
            return r.json();
        }).then(function () {
            window.location.reload();               // the same address now shows the report
        }).catch(function () {
            submitting = false;
            finishButton.disabled = false;
            message.textContent = 'Could not submit, press Finish again.';
        });
    }

    function tick() {
        var left = secondsLeft();
        timer.textContent = format(left);
        timer.classList.toggle('is-low', left <= 60);
        if (left <= 0) {
            clearInterval(handle);
            finish('time');
        }
    }

    finishButton.addEventListener('click', function () { finish('button'); });
    var handle = setInterval(tick, 500);
    tick();
})();
