package com.tcg.admin.controller.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.to.response.JsonResponse;

/**
 * Servlet Filter implementation class A1ErrorHandlerFilter
 */
@WebFilter(filterName = "errorHandlerFilter", urlPatterns = "/resources/*")
public class A1ErrorHandlerFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(A1ErrorHandlerFilter.class);

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// do nothing
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	    HttpServletResponse responseWrapper = (HttpServletResponse) response;
		try {
			chain.doFilter(request, response);
        } catch (AdminServiceBaseException e) {
            LOGGER.error("Admin Error", e);
            JsonResponse jsonResponse = new JsonResponse(false);
			jsonResponse.setErrorCode(e.getErrorCode());

			ObjectMapper mapper = new ObjectMapper();
			responseWrapper.setContentType("application/json");
			responseWrapper.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			mapper.writeValue(responseWrapper.getWriter(), jsonResponse);
			responseWrapper.flushBuffer();
		} catch (Exception e) {
			LOGGER.error("System Error", e);
			JsonResponse jsonResponse = new JsonResponse(false);
            jsonResponse.setErrorCode(AdminErrorCode.GENERIC_SYSTEM_ERROR);

			ObjectMapper mapper = new ObjectMapper();
			responseWrapper.setContentType("application/json");
			responseWrapper.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			mapper.writeValue(responseWrapper.getWriter(), jsonResponse);
			responseWrapper.flushBuffer();
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// do nothing
	}

}
