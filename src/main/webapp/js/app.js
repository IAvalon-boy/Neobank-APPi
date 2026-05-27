// API Jersey mapeada en web.xml como /api/*
const API_URL = "http://localhost:8080/BancoRestApi/api";

// 1. GET: Obtener cuentas por DUI
// Asegúrate de que API_URL esté apuntando correctamente, ej: "http://localhost:8080/BancoRestApi/api"

async function consultarCuentas() {
    const dui = document.getElementById('inputDui').value.trim();
    const contenedor = document.getElementById('resultadoCuentas');

    // Feedback de búsqueda futurista
    contenedor.innerHTML = "<div class='flex-group'><span class='status-indicator' style='background: #4f46e5; box-shadow: 0 0 10px #4f46e5;'></span><p style='color: #4f46e5;'>Escaneando bases de datos globales...</p></div>";

    if (!dui) {
        contenedor.innerHTML = "<div class='notification-area'><p class='error'>❌ El protocolo de búsqueda requiere un DUI válido.</p></div>";
        return;
    }

    try {
        const response = await fetch(`${API_URL}/cuentas/${dui}`);

        if (!response.ok) {
            contenedor.innerHTML = "<div class='notification-area'><p class='error'>❌ Error 404: Sujeto o cuentas no localizados.</p></div>";
            return;
        }

        const cuentas = await response.json();

        // Generación dinámica del Grid de Tarjetas Virtuales
        let html = "<div class='accounts-grid'>";

        cuentas.forEach(cuenta => {
            html += `
                <div class="virtual-card" onclick="document.getElementById('numCuenta').value = '${cuenta.numeroCuenta}'">
                    <div class="card-top">
                        <span class="card-type">Active NexAccount</span>
                        <div class="chip"></div>
                    </div>
                    <p class="acc-number">${cuenta.numeroCuenta}</p>
                    <div class="card-bottom">
                        <div>
                            <span class="balance-label">Balance Disponible</span>
                            <p class="acc-balance">$${cuenta.saldo.toFixed(2)}</p>
                        </div>
                        <div class="logo-mini logo" style="font-size: 1rem; padding-bottom:0">N<span class="purple-text">B</span></div>
                    </div>
                </div>`;
        });

        html += "</div>";
        contenedor.innerHTML = html;

    } catch (error) {
        contenedor.innerHTML = `<div class='notification-area'><p class='error'>⚠️ Error de Enlace: Red inestable. Reintente.</p></div>`;
    }
}

// 2 & 3. POST: Abonar y Retirar Efectivo
async function procesarTransaccion(endpoint) {
    const numeroCuenta = document.getElementById('numCuenta').value.trim();
    const monto = parseFloat(document.getElementById('monto').value);
    const contenedor = document.getElementById('resultadoTransaccion');

    if (!numeroCuenta || isNaN(monto) || monto <= 0) {
        contenedor.innerHTML = "<p class='error'>Datos inválidos. Verifique número de cuenta y monto.</p>";
        return;
    }

    // Cuerpo JSON esperado por el Request de Jackson
    const data = {
        numeroCuenta: numeroCuenta,
        monto: monto
    };

    try {
        const response = await fetch(`${API_URL}/${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const resultado = await response.json();

        if (response.ok) {
            contenedor.innerHTML = `<p class="success">${resultado.mensaje || "Operación exitosa"}</p>`;
            // Si la consulta de arriba estaba abierta, la refrescamos para ver el saldo nuevo
            if(document.getElementById('inputDui').value) consultarCuentas();
        } else {
            contenedor.innerHTML = `<p class='error'>${resultado.error || "No se pudo procesar la transacción."}</p>`;
        }

    } catch (error) {
        contenedor.innerHTML = `<p class="error">Error de conexión con el servidor.</p>`;
    }
}