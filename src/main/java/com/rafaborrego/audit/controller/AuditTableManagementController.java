package com.rafaborrego.audit.controller;

import com.rafaborrego.audit.dto.TablesNamesDTO;
import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.service.AuditTableManagementFacade;
import com.rafaborrego.audit.service.DatabaseMetadataService;
import com.rafaborrego.audit.dto.TablesNamesDTO;
import com.rafaborrego.audit.service.AuditTableManagementFacade;
import com.rafaborrego.audit.service.DatabaseMetadataService;
import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/" + AuditTableManagementController.DATABASE_MANAGEMENT_CONTROLLER})
public class AuditTableManagementController {

	public final static String DATABASE_MANAGEMENT_CONTROLLER = "databasemanagement";

	private static final Logger LOG = Logger.getLogger(AuditTableManagementController.class);

	@Autowired
	private DatabaseMetadataService databaseMetadataService;

	@Autowired
	private AuditTableManagementFacade auditTableManagementFacade;


	@RequestMapping(value = "/audit-table-management", method = RequestMethod.GET)
	public String showAuditTableManagementScreen(Model model) {
		try {
			List<String> databasesNames = databaseMetadataService.getDatabasesNames();
			if(databasesNames != null && !databasesNames.isEmpty()) {
				model.addAttribute("databasesNames", databasesNames);
			}
		} catch (Exception e) {
			LOG.error("Error obtaining the databases names", e);
		}

		return "databasemanagement/audittablemanagement";
	}

	@RequestMapping(value = "/database-tables-names", method = RequestMethod.GET)
	public @ResponseBody TablesNamesDTO getDatabaseTablesNames(@RequestParam String databaseName) {
		boolean success = false;
		TablesNamesDTO result = new TablesNamesDTO();
		try {
			if(StringUtils.hasText(databaseName)) {
				List<String> dataTablesNames = databaseMetadataService.getDataTablesNames(databaseName);
				if(dataTablesNames != null && !dataTablesNames.isEmpty()) {
					result.setTablesNames(dataTablesNames);
				}
				success = true;
			} else {
				result.setResultMessage("The database's name was not received. Please consult the administrator.");
			}
		} catch(DatabaseManagementException e) {
			LOG.error(e.getMessage(), e);
			result.setResultMessage(e.getMessage());
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			result.setResultMessage("There was an error getting the tables. Please consult the administrator.");
		}

		result.setSuccess(success);

		return result;
	}

	@RequestMapping(value = "/generate-audit-tables-creation-scripts", produces = "text/plain", method = RequestMethod.POST)
	public @ResponseBody String generateAuditTablesCreationScript(@RequestParam String databaseName,
	                                                              @RequestParam String tablesNames,
	                                                              HttpServletResponse response) {
		try {
			List<String> parsedTablesNames = Arrays.asList(tablesNames.split(","));
			String scriptContent = auditTableManagementFacade.generateAuditTablesScript(databaseName, parsedTablesNames);
			addScriptContentToResponse(scriptContent, response);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	private void addScriptContentToResponse(String scriptContent, HttpServletResponse response)
			throws DatabaseManagementException {
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Type", "text/plain; charset=UTF-8");
			response.getWriter().write(scriptContent);
			response.flushBuffer();
		} catch (IOException e) {
			throw new DatabaseManagementException("There was an error writing the content to the response", e);
		}
	}
}
