(() => {
    const storageKey = 'omnicare-theme';
    let selectedTheme = 'light';

    try {
        selectedTheme = localStorage.getItem(storageKey) === 'dark' ? 'dark' : 'light';
    } catch (error) {
        selectedTheme = 'light';
    }

    document.documentElement.dataset.theme = selectedTheme;

    const initializeThemeButtons = () => {
        const updateButtons = () => {
            const isDark = document.documentElement.dataset.theme === 'dark';
            const label = `Switch to ${isDark ? 'light' : 'dark'} mode`;

            document.querySelectorAll('.theme-toggle').forEach((button) => {
                const icon = button.querySelector('i');
                button.setAttribute('aria-label', label);
                button.setAttribute('title', label);
                button.setAttribute('aria-pressed', String(isDark));
                icon?.classList.toggle('fa-sun', isDark);
                icon?.classList.toggle('fa-moon', !isDark);
            });
        };

        const applyTheme = (theme) => {
            document.documentElement.dataset.theme = theme;
            updateButtons();

            try {
                localStorage.setItem(storageKey, theme);
            } catch (error) {
                return;
            }
        };

        document.querySelectorAll('.theme-toggle').forEach((button) => {
            button.addEventListener('click', () => {
                const nextTheme = document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark';
                applyTheme(nextTheme);
            });
        });

        updateButtons();
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializeThemeButtons, { once: true });
    } else {
        initializeThemeButtons();
    }
})();