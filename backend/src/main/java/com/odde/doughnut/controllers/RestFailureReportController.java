package com.odde.doughnut.controllers;

import com.odde.doughnut.entities.FailureReport;
import com.odde.doughnut.exceptions.UnexpectedNoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.services.GithubService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/failure-reports")
class RestFailureReportController {
  private final ModelFactoryService modelFactoryService;
  private final GithubService realGithubService;
  private UserModel currentUser;

  public RestFailureReportController(
      ModelFactoryService modelFactoryService,
      GithubService realGithubService,
      UserModel currentUser) {
    this.modelFactoryService = modelFactoryService;
    this.realGithubService = realGithubService;
    this.currentUser = currentUser;
  }

  @GetMapping("")
  public Iterable<FailureReport> failureReports() throws UnexpectedNoAccessRightException {
    currentUser.assertLoggedIn();
    currentUser.assertAdminAuthorization();
    return modelFactoryService.failureReportRepository.findAll();
  }

  static class FailureReportForView {
    public FailureReport failureReport;
    public String githubIssueUrl;
  }

  @GetMapping("/{failureReport}")
  public FailureReportForView show(
      @PathVariable("failureReport") @Schema(type = "integer") FailureReport failureReport)
      throws UnexpectedNoAccessRightException {
    currentUser.assertLoggedIn();
    currentUser.assertAdminAuthorization();
    FailureReportForView failureReportForView = new FailureReportForView();
    failureReportForView.failureReport = failureReport;
    failureReportForView.githubIssueUrl =
        realGithubService.getIssueUrl(failureReport.getIssueNumber());
    return failureReportForView;
  }
}
