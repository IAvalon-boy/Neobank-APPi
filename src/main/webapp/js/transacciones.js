const NexBankTransacciones = (function () {

    function init() {
        const cuentaGuardada = sessionStorage.getItem('cuentaSeleccionada');
        if (cuentaGuardada) {
            document.getElementById('numCuenta').value = cuentaGuardada;
        }

        document.getElementById('btnAbonar').addEventListener('click', function () {
            procesarTransaccion('abonarefectivo');
        });
        document.getElementById('btnRetirar').addEventListener('click', function () {
            procesarTransaccion('retirarefectivo');
        });
    }

    async function procesarTransaccion(endpoint) {
        const numeroCuenta = document.getElementById('numCuenta').value.trim();
        const monto = parseFloat(document.getElementById('monto').value);
        const contenedor = document.getElementById('resultadoTransaccion');

        const errorCuenta = NexBank.validarCuenta(numeroCuenta);
        if (errorCuenta) {
            contenedor.innerHTML = "<p class='error'>" + errorCuenta + "</p>";
            return;
        }

        const errorMonto = NexBank.validarMonto(monto);
        if (errorMonto) {
            contenedor.innerHTML = "<p class='error'>" + errorMonto + "</p>";
            return;
        }

        const data = {
            numeroCuenta: numeroCuenta,
            monto: monto
        };

        contenedor.innerHTML = "<p class='loading-text'>Procesando transacción...</p>";

        try {
            const response = await fetch(NexBank.getApiUrl() + '/' + endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const resultado = await NexBank.parseJson(response);

            if (response.ok) {
                contenedor.innerHTML = "<p class='success'>" + (resultado.mensaje || 'Operación exitosa') + "</p>";
                sessionStorage.setItem('cuentaSeleccionada', numeroCuenta);
            } else {
                contenedor.innerHTML = "<p class='error'>" + NexBank.mensajeError(response, resultado) + "</p>";
            }
        } catch (error) {
            contenedor.innerHTML = "<p class='error'>Error de conexión con el servidor.</p>";
        }
    }

    return { init };
})();
