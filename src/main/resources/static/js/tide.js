/* ScrollTide: water level that rises with the scroll position, plus reveal-on-scroll.
   Everything is decorative, so any failure here must never break the page. */
(function () {
    'use strict';

    var reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var root = document.documentElement;

    function wavePath() {
        // Two full periods across 2880 units; shifting by half of the width loops seamlessly.
        return 'M0 60 Q180 0 360 60 T720 60 T1080 60 T1440 60 T1800 60 T2160 60 T2520 60 T2880 60 V120 H0 Z';
    }

    function buildTide() {
        var svg = function (cls) {
            return '<svg class="tide-wave ' + cls + '" viewBox="0 0 2880 120" preserveAspectRatio="none" aria-hidden="true">'
                + '<path d="' + wavePath() + '"/></svg>';
        };
        var tide = document.createElement('div');
        tide.className = 'tide';
        tide.setAttribute('aria-hidden', 'true');
        tide.innerHTML = '<div class="tide-water">' + svg('back') + svg('front') + '<div class="tide-fill"></div></div>';
        document.body.insertBefore(tide, document.body.firstChild);

        var bar = document.createElement('div');
        bar.className = 'tide-progress';
        bar.setAttribute('aria-hidden', 'true');
        document.body.insertBefore(bar, document.body.firstChild);
    }

    function setLevel(value) {
        root.style.setProperty('--tide', Math.max(0, Math.min(1, value)).toFixed(4));
    }

    function trackScroll() {
        // Demo/testing hook: /?tide=0.6 freezes the water at 60 percent.
        var forced = new URLSearchParams(window.location.search).get('tide');
        if (forced !== null && !isNaN(parseFloat(forced))) {
            setLevel(parseFloat(forced));
            return;
        }
        var ticking = false;
        function update() {
            var max = root.scrollHeight - window.innerHeight;
            // Short pages cannot scroll, so keep the water at a gentle fixed height instead of leaving it empty.
            setLevel(max > 40 ? window.scrollY / max : 0.45);
            ticking = false;
        }
        window.addEventListener('scroll', function () {
            if (!ticking) {
                ticking = true;
                window.requestAnimationFrame(update);
            }
        }, { passive: true });
        window.addEventListener('resize', update);
        update();
    }

    function reveal() {
        if (!('IntersectionObserver' in window)) {
            return;
        }
        var selector = '.card, .step, .stat, .stat-card, .guide-section, .chapter, .callout, '
            + '.final, .chip, .about-stat, .detail-list, .lbl-row';
        var items = document.querySelectorAll(selector);
        var seenPerParent = new Map();

        items.forEach(function (el) {
            var n = seenPerParent.get(el.parentElement) || 0;
            seenPerParent.set(el.parentElement, n + 1);
            el.style.setProperty('--d', Math.min(n, 6) * 70 + 'ms');
            el.classList.add('reveal');
        });

        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (!entry.isIntersecting) {
                    return;
                }
                var el = entry.target;
                el.classList.add('in');
                observer.unobserve(el);
                // Once shown, drop the helper classes so hover effects defined elsewhere keep working.
                window.setTimeout(function () {
                    el.classList.remove('reveal', 'in');
                    el.style.removeProperty('--d');
                }, 1200);
            });
        }, { threshold: 0, rootMargin: '0px 0px -4% 0px' });

        items.forEach(function (el) { observer.observe(el); });

        // Safety net: never leave content hidden, even if the observer misbehaves.
        window.setTimeout(function () {
            document.querySelectorAll('.reveal:not(.in)').forEach(function (el) { el.classList.add('in'); });
        }, 2500);
    }

    function init() {
        try {
            if (reduceMotion) {
                return;
            }
            if (document.body.classList.contains('welcome')) {
                buildTide();                  // the rising water and progress line are for the welcome page only
                trackScroll();
            }
            reveal();
        } catch (e) {
            // Decorative only: ignore.
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
