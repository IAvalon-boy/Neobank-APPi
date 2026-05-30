const NexBankConsulta = (function () {

    function init() {
        document.getElementById('btnBuscar').addEventListener('click', consultarCuentas);
        document.getElementById('inputDui').addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                consultarCuentas();
            }
        });
    }

    async function consultarCuentas() {
        const dui = document.getElementById('inputDui').value.trim();
        const contenedor = document.getElementById('resultadoCuentas');

        const errorDui = NexBank.validarDui(dui);
        if (errorDui) {
            contenedor.innerHTML = "<div class='notification-area'><p class='error'>" + errorDui + "</p></div>";
            return;
        }

        contenedor.innerHTML = "<div class='flex-group'><span class='status-indicator loading'></span><p class='loading-text'>Consultando cuentas...</p></div>";

        try {
            const response = await fetch(NexBank.getApiUrl() + '/cuentas/' + encodeURIComponent(dui));
            const body = await NexBank.parseJson(response);

            if (!response.ok) {
                contenedor.innerHTML = "<div class='notification-area'><p class='error'>" + NexBank.mensajeError(response, body) + "</p></div>";
                return;
            }

            if (!body.length) {
                contenedor.innerHTML = "<div class='notification-area'><p class='error'>El cliente no tiene cuentas registradas.</p></div>";
                return;
            }

            let html = "<div class='accounts-grid'>";
            body.forEach(function (cuenta) {
                html += `
                    <div class="virtual-card" data-cuenta="${cuenta.numeroCuenta}">
                        <div class="card-top">
                            <span class="card-type">Active NexAccount</span>
                            <div class="chip"></div>
                        </div>
                        <p class="acc-number">${cuenta.numeroCuenta}</p>
                        <div class="card-bottom">
                            <div>
                                <span class="balance-label">Balance Disponible</span>
                                <p class="acc-balance">$${Number(cuenta.saldo).toFixed(2)}</p>
                            </div>
                            <div class="logo-mini logo" style="font-size: 1rem; padding-bottom:0">N<span class="purple-text">B</span></div>
                        </div>
                        <p class="card-hint">Clic para operar en caja</p>
                    </div>`;
            });
            html += "</div>";
            contenedor.innerHTML = html;

            contenedor.querySelectorAll('.virtual-card').forEach(function (card) {
                card.addEventListener('click', function () {
                    const numero = card.getAttribute('data-cuenta');
                    sessionStorage.setItem('cuentaSeleccionada', numero);
                    window.location.href = NexBank.getContextPath() + 'transacciones';
                });
            });

        } catch (error) {
            contenedor.innerHTML = "<div class='notification-area'><p class='error'>Error de conexión con el servidor.</p></div>";
        }
    }

    return { init };
})();
