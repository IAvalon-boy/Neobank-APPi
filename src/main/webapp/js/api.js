/**
 * Utilidades compartidas del cliente JavaScript (consume API REST Jersey).
 */
const NexBank = (function () {
    let contextPath = '/';
    let apiUrl = '/api';

    const PATRON_DUI = /^\d{8}-\d$/;
    const PATRON_CUENTA = /^\d{3}-\d{6}-\d{2}$/;

    function init(ctx, api) {
        contextPath = ctx.endsWith('/') ? ctx : ctx + '/';
        apiUrl = api.startsWith('http') ? api : normalizeApiUrl(api);
    }

    function normalizeApiUrl(api) {
        if (api.startsWith('/')) {
            return window.location.origin + api;
        }
        return window.location.origin + contextPath + 'api';
    }

    function getContextPath() {
        return contextPath;
    }

    function getApiUrl() {
        return apiUrl;
    }

    function validarDui(dui) {
        if (!dui || !dui.trim()) {
            return 'El DUI es obligatorio.';
        }
        if (!PATRON_DUI.test(dui.trim())) {
            return 'Formato de DUI inválido. Use el formato 12345678-9.';
        }
        return null;
    }

    function validarCuenta(numero) {
        if (!numero || !numero.trim()) {
            return 'El número de cuenta es obligatorio.';
        }
        if (!PATRON_CUENTA.test(numero.trim())) {
            return 'Formato de cuenta inválido. Use el formato 000-000000-00.';
        }
        return null;
    }

    function validarMonto(monto) {
        if (isNaN(monto) || monto <= 0) {
            return 'El monto debe ser mayor a cero.';
        }
        return null;
    }

    async function parseJson(response) {
        try {
            return await response.json();
        } catch (e) {
            return {};
        }
    }

    function mensajeError(response, body) {
        return body.error || ('Error del servidor (' + response.status + ').');
    }

    return {
        init,
        getContextPath,
        getApiUrl,
        validarDui,
        validarCuenta,
        validarMonto,
        parseJson,
        mensajeError
    };
})();
