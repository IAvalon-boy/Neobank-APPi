package com.banco.api;

import com.banco.dto.CuentaListaItem;
import com.banco.dto.ErrorResponse;
import com.banco.dto.MensajeResponse;
import com.banco.dto.TransaccionRequest;
import com.banco.service.BancoException;
import com.banco.service.BancoService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * API REST (Jersey + Jackson) para el dependiente bancario.
 */
@Path("/")
public class BancoResource {

    @Context
    private ServletContext servletContext;

    private BancoService bancoService;

    private BancoService service() {
        if (bancoService == null) {
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            if (ctx == null) {
                throw new IllegalStateException("Contexto Spring no inicializado.");
            }
            bancoService = ctx.getBean(BancoService.class);
        }
        return bancoService;
    }

    @GET
    @Path("cuentas/{dui}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCuentasPorDui(@PathParam("dui") String dui) {
        try {
            List<CuentaListaItem> items = service().listarCuentasPorDui(dui);
            return Response.ok(items).build();
        } catch (BancoException e) {
            return Response.status(e.getHttpStatus())
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("abonarefectivo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response abonar(TransaccionRequest body) {
        try {
            MensajeResponse resp = service().abonar(body);
            return Response.ok(resp).build();
        } catch (BancoException e) {
            return Response.status(e.getHttpStatus())
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("retirarefectivo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retirar(TransaccionRequest body) {
        try {
            MensajeResponse resp = service().retirar(body);
            return Response.ok(resp).build();
        } catch (BancoException e) {
            return Response.status(e.getHttpStatus())
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
