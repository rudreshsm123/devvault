/* Certificates: works out which certificates the learner has earned from their own progress,
   and fills in the printable certificate (name, date, certificate id). */
(function () {
    'use strict';

    function ids(text) {
        return (text || '').split(',').filter(function (s) { return s.length > 0; });
    }

    function countDone(data, kind, list) {
        return list.filter(function (id) { return window.DevVaultProgress.isDone(data, kind, id); }).length;
    }

    function solvedProblems(data) {
        return Object.keys(data.problem).filter(function (id) {
            return window.DevVaultProgress.isDone(data, 'problem', id);
        }).length;
    }

    /** Progress of one requirement: {have, need}. */
    function requirement(el, data) {
        var lessons = ids(el.dataset.lessons);
        if (lessons.length > 0) {
            return { have: countDone(data, 'lesson', lessons), need: lessons.length, unit: 'lessons' };
        }
        var need = parseInt(el.dataset.problems, 10) || 0;
        return { have: solvedProblems(data), need: need, unit: 'problems solved' };
    }

    /* ---------- /certificates ---------- */
    function initList() {
        var cards = document.querySelectorAll('.cert-card');
        if (cards.length === 0) {
            return;
        }
        window.DevVaultProgress.load().then(function (data) {
            var earnedCount = 0;
            cards.forEach(function (card) {
                var r = requirement(card, data);
                var earned = r.need > 0 && r.have >= r.need;
                var status = card.querySelector('.cert-status');
                var link = card.querySelector('.cert-open');
                if (earned) {
                    earnedCount++;
                    card.classList.add('earned');
                    status.textContent = 'Earned';
                    link.classList.remove('disabled');
                    link.removeAttribute('aria-disabled');
                } else {
                    status.textContent = Math.min(r.have, r.need) + ' / ' + r.need + ' ' + r.unit;
                    link.classList.add('disabled');
                    link.setAttribute('aria-disabled', 'true');
                    link.addEventListener('click', function (e) { e.preventDefault(); });
                }
            });
            var summary = document.getElementById('cert-summary');
            if (summary) {
                summary.textContent = 'You have earned ' + earnedCount + ' of ' + cards.length + ' certificates.';
            }
        });
    }

    /* ---------- /certificate?type=... ---------- */
    function hash(text) {                       // small non-cryptographic hash, only used to make a readable id
        var h = 2166136261;
        for (var i = 0; i < text.length; i++) {
            h ^= text.charCodeAt(i);
            h = Math.imul(h, 16777619);
        }
        return (h >>> 0).toString(16).toUpperCase().padStart(8, '0');
    }

    function initCertificate() {
        var cert = document.getElementById('cert');
        if (!cert) {
            return;
        }
        var message = document.getElementById('cert-msg');
        var nameInput = document.getElementById('cert-name');
        var nameOut = document.getElementById('cert-name-out');
        var dateOut = document.getElementById('cert-date');
        var idOut = document.getElementById('cert-id');
        var stored = null;
        try { stored = window.localStorage.getItem('devvault.certname'); } catch (e) { /* ignore */ }
        nameInput.value = stored || window.DevVaultProgress.user() || '';

        var today = new Date();
        var dateText = today.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
        dateOut.textContent = dateText;

        function render() {
            var name = nameInput.value.trim();
            nameOut.textContent = name || 'Your Name';
            nameOut.classList.toggle('placeholder', !name);
            idOut.textContent = 'DV-' + hash((name || '-') + '|' + cert.dataset.type + '|' + today.toISOString().slice(0, 10));
            try { window.localStorage.setItem('devvault.certname', name); } catch (e) { /* ignore */ }
        }
        nameInput.addEventListener('input', render);
        render();

        document.getElementById('cert-print').addEventListener('click', function () {
            if (!nameInput.value.trim()) {
                message.textContent = 'Please type the name you want on the certificate first.';
                nameInput.focus();
                return;
            }
            window.print();
        });

        window.DevVaultProgress.load().then(function (data) {
            var r = requirement(cert, data);
            if (r.need > 0 && r.have >= r.need) {
                cert.hidden = false;
                message.textContent = 'Congratulations! Type your name, then use "Print / Save as PDF".';
            } else {
                message.textContent = 'Not earned yet: ' + Math.min(r.have, r.need) + ' of ' + r.need + ' ' + r.unit
                    + ' finished. Keep going and come back.';
                document.getElementById('cert-print').disabled = true;
            }
        });
    }

    function init() {
        if (!window.DevVaultProgress) {
            return;
        }
        initList();
        initCertificate();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
