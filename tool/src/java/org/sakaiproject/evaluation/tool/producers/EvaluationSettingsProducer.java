/******************************************************************************
 * EvaluationSettingsProducer.java - created by kahuja@vt.edu on Oct 05, 2006
 * 
 * Copyright (c) 2007 Virginia Polytechnic Institute and State University
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Kapil Ahuja (kahuja@vt.edu)
 * Rui Feng (fengr@vt.edu)
 *****************************************************************************/

package org.sakaiproject.evaluation.tool.producers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.sakaiproject.evaluation.logic.EvalAuthoringService;
import org.sakaiproject.evaluation.logic.EvalEvaluationService;
import org.sakaiproject.evaluation.logic.EvalSettings;
import org.sakaiproject.evaluation.logic.entity.EvalCategoryEntityProvider;
import org.sakaiproject.evaluation.logic.externals.EvalExternalLogic;
import org.sakaiproject.evaluation.model.constant.EvalConstants;
import org.sakaiproject.evaluation.tool.EvalToolConstants;
import org.sakaiproject.evaluation.tool.EvaluationBean;
import org.sakaiproject.evaluation.tool.viewparams.EmailViewParameters;
import org.sakaiproject.evaluation.tool.viewparams.TemplateViewParameters;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.rsf.components.UIBoundBoolean;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInitBlock;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIDisabledDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * This producer is used to render the evaluation settings page. It is used when
 * user either creates a new evaluation (coming forward from "Start Evaluation"
 * page or coming backward from "Assign Evaluation to courses" page) or from
 * control panel to edit the existing settings.
 * 
 * @author Kapil Ahuja (kahuja@vt.edu)
 * @author Rui Feng (fengr@vt.edu)
 */
public class EvaluationSettingsProducer implements ViewComponentProducer, NavigationCaseReporter {

   public static final String VIEW_ID = "evaluation_settings";
   public String getViewID() {
      return VIEW_ID;
   }

   private EvalSettings settings;
   public void setSettings(EvalSettings settings) {
      this.settings = settings;
   }

   private EvaluationBean evaluationBean;
   public void setEvaluationBean(EvaluationBean evaluationBean) {
      this.evaluationBean = evaluationBean;
   }

   private EvalExternalLogic externalLogic;
   public void setExternalLogic(EvalExternalLogic externalLogic) {
      this.externalLogic = externalLogic;
   }

   private EvalEvaluationService evaluationService;
   public void setEvaluationService(EvalEvaluationService evaluationService) {
      this.evaluationService = evaluationService;
   }

   private EvalAuthoringService authoringService;
   public void setAuthoringService(EvalAuthoringService authoringService) {
      this.authoringService = authoringService;
   }

   private FormatAwareDateInputEvolver dateevolver;
   public void setDateEvolver(FormatAwareDateInputEvolver dateevolver) {
      this.dateevolver = dateevolver;
   }

   private Locale locale;
   public void setLocale(Locale locale) {
      this.locale = locale;
   }


   /* (non-Javadoc)
    * @see uk.org.ponder.rsf.view.ComponentProducer#fillComponents(uk.org.ponder.rsf.components.UIContainer, uk.org.ponder.rsf.viewstate.ViewParameters, uk.org.ponder.rsf.view.ComponentChecker)
    */
   public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

      // local variables used in the render logic
      String currentUserId = externalLogic.getCurrentUserId();
      boolean userAdmin = externalLogic.isUserAdmin(currentUserId);
      boolean createTemplate = authoringService.canCreateTemplate(currentUserId);
      boolean beginEvaluation = evaluationService.canBeginEvaluation(currentUserId);

      DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
      DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);

      /*
       * top links here
       */
      UIInternalLink.make(tofill, "summary-link", 
            UIMessage.make("summary.page.title"), 
            new SimpleViewParameters(SummaryProducer.VIEW_ID));

      if (userAdmin) {
         UIInternalLink.make(tofill, "administrate-link", 
               UIMessage.make("administrate.page.title"),
               new SimpleViewParameters(AdministrateProducer.VIEW_ID));
         UIInternalLink.make(tofill, "control-scales-link",
               UIMessage.make("controlscales.page.title"),
               new SimpleViewParameters(ControlScalesProducer.VIEW_ID));
      }

      if (createTemplate) {
         UIInternalLink.make(tofill, "control-templates-link",
               UIMessage.make("controltemplates.page.title"), 
               new SimpleViewParameters(ControlTemplatesProducer.VIEW_ID));
         UIInternalLink.make(tofill, "control-items-link",
               UIMessage.make("controlitems.page.title"), 
               new SimpleViewParameters(ControlItemsProducer.VIEW_ID));
      }

      if (beginEvaluation) {
         UIInternalLink.make(tofill, "control-evaluations-link",
               UIMessage.make("controlevaluations.page.title"),
               new SimpleViewParameters(ControlEvaluationsProducer.VIEW_ID));
      } else {
         throw new SecurityException("User attempted to access " + 
               VIEW_ID + " when they are not allowed");
      }


      UIForm form = UIForm.make(tofill, "evalSettingsForm");
      UIMessage.make(form, "settings-desc-header", "evalsettings.settings.desc.header");
      UIOutput.make(form, "evaluationTitle", null, "#{evaluationBean.eval.title}");

      Date today = new Date();
      UIMessage.make(tofill, "current_date", "evalsettings.dates.current", 
            new Object[] { dateFormat.format(today), timeFormat.format(today) });

      // retrieve the global setting for use of date only or date and time picker
      Boolean useDateTime = (Boolean) settings.get(EvalSettings.EVAL_USE_DATE_TIME);
      if (useDateTime == null) { useDateTime = Boolean.FALSE; }

      UIInput startDate = UIInput.make(form, "startDate:", "#{evaluationBean.startDate}");	
      if (evaluationBean.eval.getId() != null) {
         if (today.before(evaluationBean.eval.getStartDate())) {
            // queued evalution
            UIInput.make(form, "evalStatus", null, "queued");
         } else {
            // started evaluation
            startDate.decorators = new DecoratorList(new UIDisabledDecorator());
            UIInput.make(form, "evalStatus", null, "active");
         }
      } else {
         UIInput.make(form, "evalStatus", null, "new");
      }
      if (useDateTime) {
         dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);		   
      } else {
         dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);		   
      }
      dateevolver.evolveDateInput(startDate, evaluationBean.startDate);

      UIMessage.make(form, "eval-due-date-header", "evalsettings.due.date.header");
      UIMessage.make(form, "eval-due-date-desc", "evalsettings.due.date.desc");
      UIInput dueDate = UIInput.make(form, "dueDate:", "#{evaluationBean.dueDate}");
      if (useDateTime) {
         dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);         
      } else {
         dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);        
      }
      dateevolver.evolveDateInput(dueDate, evaluationBean.dueDate);

      // Show the "Stop date" text box only if allowed in the System settings
      if (((Boolean) settings.get(EvalSettings.EVAL_USE_STOP_DATE)).booleanValue()) {
         UIBranchContainer showStopDate = UIBranchContainer.make(form, "showStopDate:");
         UIMessage.make(showStopDate, "eval-stop-date-header", "evalsettings.stop.date.header");
         UIMessage.make(showStopDate, "eval-stop-date-desc", "evalsettings.stop.date.desc");
         UIInput stopDate = UIInput.make(showStopDate, "stopDate:", "#{evaluationBean.stopDate}");
         if (useDateTime) {
            dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);         
         } else {
            dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);        
         }
         dateevolver.evolveDateInput(stopDate, evaluationBean.stopDate);
      }

      // show the view date only if allowed by system settings
      if (((Boolean) settings.get(EvalSettings.EVAL_USE_VIEW_DATE)).booleanValue()) {
         UIBranchContainer showViewDate = UIBranchContainer.make(form, "showViewDate:");
         UIMessage.make(showViewDate, "eval-view-date-header", "evalsettings.view.date.header");
         UIMessage.make(showViewDate, "eval-view-date-desc", "evalsettings.view.date.desc");
         UIInput viewDate = UIInput.make(showViewDate, "viewDate:", "#{evaluationBean.viewDate}");
         if (useDateTime) {
            dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);         
         } else {
            dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);        
         }
         dateevolver.evolveDateInput(viewDate, evaluationBean.viewDate);
      }


      UIMessage.make(form, "eval-results-viewable-header", "evalsettings.results.viewable.header");
      UIMessage.make(form, "eval-results-viewable-private-start", "evalsettings.results.viewable.private.start");
      UIMessage.make(form, "eval-results-viewable-private-middle", "evalsettings.results.viewable.private.middle");
      UIOutput.make(form, "userInfo", externalLogic.getUserDisplayName(externalLogic.getCurrentUserId()));
      UIMessage.make(form, "private-warning-desc", "evalsettings.private.warning.desc");
      UIBoundBoolean.make(form, "resultsPrivate", "#{evaluationBean.eval.resultsPrivate}");

      // Variable is used to decide whether to show the view date textbox for student and instructor separately or not.
      boolean sameViewDateForAll = ((Boolean) settings.get(EvalSettings.EVAL_USE_SAME_VIEW_DATES)).booleanValue();
      // If "EvalSettings.STUDENT_VIEW_RESULTS" is set as configurable i.e. NULL in the database OR is set as TRUE in database,
      // then show the checkbox. Else do not show the checkbox and just bind the value to FALSE.
      Boolean studentSetting = (Boolean) settings.get(EvalSettings.STUDENT_VIEW_RESULTS);

      if (studentSetting == null || studentSetting.booleanValue()) {
         UIBranchContainer showResultsToStudents = UIBranchContainer.make(form, "showResultsToStudents:");
         UIMessage.make(showResultsToStudents, "eval-results-viewable-students", "evalsettings.results.viewable.students");

         // If system setting was null implies normal checkbox. If it was TRUE then
         // a disabled selected checkbox
         if (studentSetting == null) {
            UIBoundBoolean.make(showResultsToStudents, "studentViewResults", "#{evaluationBean.studentViewResults}", studentSetting);
         } else {
            // Display only (disabled) and selected check box
            UIBoundBoolean stuViewCheckbox = UIBoundBoolean.make(showResultsToStudents, "studentViewResults", studentSetting);
            setDisabledAttribute(stuViewCheckbox);

            // As we have disabled the check box => RSF will not bind the value =>
            // binding it explicitly.
            form.parameters.add(new UIELBinding("#{evaluationBean.studentViewResults}", studentSetting));
         }

         // If same view date all then show a label else show a text box.
         if (sameViewDateForAll) {
            UIMessage.make(showResultsToStudents, "eval-results-stu-inst-date-label", "evalsettings.results.stu.inst.date.label");
         } else {
            UIInput studentsDate = UIInput.make(showResultsToStudents, "studentsDate:", "#{evaluationBean.studentsDate}");
            if (useDateTime) {
               dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);         
            } else {
               dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);        
            }
            dateevolver.evolveDateInput(studentsDate, evaluationBean.studentsDate);
         }
      } else {
         form.parameters.add(new UIELBinding("#{evaluationBean.studentViewResults}", Boolean.FALSE));
      }

      /*
       * (non-javadoc) If "EvalSettings.INSTRUCTOR_ALLOWED_VIEW_RESULTS" is set as
       * configurable i.e. NULL in the database OR is set as TRUE in database,
       * then show the checkbox. Else do not show the checkbox and just bind the
       * value to FALSE.
       */
      studentSetting = (Boolean) settings.get(EvalSettings.INSTRUCTOR_ALLOWED_VIEW_RESULTS);
      if (studentSetting == null || studentSetting.booleanValue()) {

         UIBranchContainer showResultsToInst = UIBranchContainer.make(form, "showResultsToInst:");
         UIMessage.make(showResultsToInst, "eval-results-viewable-instructors", "evalsettings.results.viewable.instructors");

         // If system setting was null implies normal checkbox. If it was TRUE then
         // a disabled selected checkbox
         if (studentSetting == null) {
            UIBoundBoolean.make(showResultsToInst, "instructorViewResults", "#{evaluationBean.instructorViewResults}", studentSetting);
         } else {
            // Display only (disabled) and selected check box
            UIBoundBoolean instViewCheckbox = UIBoundBoolean.make(showResultsToInst, "instructorViewResults", studentSetting);
            setDisabledAttribute(instViewCheckbox);

            // As we have disabled the check box => RSF will not bind the value => binding it explicitly.
            form.parameters.add(new UIELBinding("#{evaluationBean.instructorViewResults}", studentSetting));
         }

         // If same view date all then show a label else show a text box.
         if (sameViewDateForAll) {
            UIMessage.make(showResultsToInst, "eval-results-stu-inst-date-label", "evalsettings.results.stu.inst.date.label");
         } else {
            UIInput instructorsDate = UIInput.make(showResultsToInst, "instructorsDate:", "#{evaluationBean.instructorsDate}");
            if (useDateTime) {
               dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);         
            } else {
               dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);        
            }
            dateevolver.evolveDateInput(instructorsDate, evaluationBean.instructorsDate);
         }
      } else {
         form.parameters.add(new UIELBinding("#{evaluationBean.instructorViewResults}", Boolean.FALSE));
      }

      UIMessage.make(form, "eval-results-viewable-admin-note", "evalsettings.results.viewable.admin.note");

      /*
       * (non-javadoc) If "EvalSettings.STUDENT_ALLOWED_LEAVE_UNANSWERED" is set
       * as configurable i.e. NULL in the database OR is set as TRUE in database,
       * then show the checkbox. Else do not show the checkbox and just bind the
       * value to FALSE.
       * 
       * Note: The variable showStudentCompletionHeader is used to show
       * "student-completion-settings-header" It is true only if either of the
       * three "if's" below are evaluated to true.
       */
      boolean showStudentCompletionHeader = false;
      studentSetting = (Boolean) settings.get(EvalSettings.STUDENT_ALLOWED_LEAVE_UNANSWERED);
      if (studentSetting == null || studentSetting.booleanValue()) {

         showStudentCompletionHeader = true;
         UIBranchContainer showBlankQuestionAllowedToStut = UIBranchContainer.make(form, "showBlankQuestionAllowedToStut:");
         UIMessage.make(showBlankQuestionAllowedToStut, "blank-responses-allowed-desc", "evalsettings.blank.responses.allowed.desc");
         UIMessage.make(showBlankQuestionAllowedToStut, "blank-responses-allowed-note", "evalsettings.blank.responses.allowed.note");

         // If system setting was null implies normal checkbox. If it was TRUE then
         // a disabled selected checkbox
         if (studentSetting == null) {
            UIBoundBoolean.make(showBlankQuestionAllowedToStut, "blankResponsesAllowed", "#{evaluationBean.eval.blankResponsesAllowed}", studentSetting);
         } else {
            // Display only (disabled) and selected check box
            UIBoundBoolean stuLeaveUnanswered = UIBoundBoolean.make(showBlankQuestionAllowedToStut, "blankResponsesAllowed", studentSetting);
            setDisabledAttribute(stuLeaveUnanswered);

            // As we have disabled the check box => RSF will not bind the value =>
            // binding it explicitly.
            form.parameters.add(new UIELBinding("#{evaluationBean.eval.blankResponsesAllowed}", studentSetting));
         }
      } else {
         form.parameters.add(new UIELBinding("#{evaluationBean.eval.blankResponsesAllowed}", Boolean.FALSE));
      }

      /*
       * (non-javadoc) If "EvalSettings.STUDENT_MODIFY_RESPONSES" is set as
       * configurable i.e. NULL in the database OR is set as TRUE in database,
       * then show the checkbox. Else do not show the checkbox and just bind the
       * value to FALSE.
       */
      studentSetting = (Boolean) settings.get(EvalSettings.STUDENT_MODIFY_RESPONSES);
      if (studentSetting == null || studentSetting.booleanValue()) {

         showStudentCompletionHeader = true;
         UIBranchContainer showModifyResponsesAllowedToStu = UIBranchContainer.make(form, "showModifyResponsesAllowedToStu:");
         UIMessage.make(showModifyResponsesAllowedToStu, "modify-responses-allowed-desc", "evalsettings.modify.responses.allowed.desc");
         UIMessage.make(showModifyResponsesAllowedToStu, "modify-responses-allowed-note", "evalsettings.modify.responses.allowed.note");

         // If system setting was null implies normal checkbox. If it was TRUE then
         // a disabled selected checkbox
         if (studentSetting == null) {
            UIBoundBoolean.make(showModifyResponsesAllowedToStu, "modifyResponsesAllowed", "#{evaluationBean.eval.modifyResponsesAllowed}", studentSetting);
         } else {
            // Display only (disabled) and selected check box
            UIBoundBoolean stuModifyResponses = UIBoundBoolean.make(showModifyResponsesAllowedToStu, "modifyResponsesAllowed", studentSetting);
            setDisabledAttribute(stuModifyResponses);

            // Since we have disabled the check box => RSF will not bind the value => binding it explicitly.
            form.parameters.add(new UIELBinding("#{evaluationBean.eval.modifyResponsesAllowed}", studentSetting));
         }
      } else {
         form.parameters.add(new UIELBinding("#{evaluationBean.eval.modifyResponsesAllowed}", Boolean.FALSE));
      }

      // Not sure why this block is here so I commented it out -AZ
//    if (false) {
//    showStudentCompletionHeader = true;
//    UIBranchContainer showUnregAllowedOption = UIBranchContainer.make(form, "showUnregAllowedOption:");
//    UIMessage.make(showUnregAllowedOption, "unregistered-allowed-desc", "evalsettings.unregistered.allowed.desc");
//    UIMessage.make(showUnregAllowedOption, "unregistered-allowed-note", "evalsettings.unregistered.allowed.note");
//    UIBoundBoolean.make(showUnregAllowedOption, "unregisteredAllowed", "#{evaluationBean.eval.unregisteredAllowed}");
//    }

      /*
       * Continued from the note above, that is show "student-completion-settings-header" only if there are any student
       * completion settings to be displayed on the page.
       */
      if (showStudentCompletionHeader) {
         UIBranchContainer showStudentCompletionDiv = UIBranchContainer.make(form, "showStudentCompletionDiv:");
         UIMessage.make(showStudentCompletionDiv, "student-completion-settings-header", "evalsettings.student.completion.settings.header");
      }

      // ADMIN SETTINGS SECTION
      UIMessage.make(form, "admin-settings-header", "evalsettings.admin.settings.header");

      UIMessage.make(form, "auth-control-instructions", "evalsettings.auth.control.instructions");
      UIMessage.make(form, "auth-control-header", "evalsettings.auth.control.header");

      UISelect authControlSelect = UISelect.make(form, "auth-control-choose", EvalToolConstants.AUTHCONTROL_VALUES, 
            EvalToolConstants.AUTHCONTROL_LABELS, "#{evaluationBean.eval.authControl}").setMessageKeys();

      if (externalLogic.isUserAdmin(externalLogic.getCurrentUserId())) {
         // If the person is an admin (any kind), then we need to show these instructor opt in/out settings

         UIMessage.make(form, "instructor-opt-instructions", "evalsettings.admin.settings.instructions");
         UIMessage.make(form, "instructor-opt-header", "evalsettings.instructor.opt.desc");

         // If "EvalSettings.INSTRUCTOR_MUST_USE_EVALS_FROM_ABOVE" is set as configurable 
         // i.e. NULL in the database then show the instructor opt select box. Else just show the value as label
         String instUseFromAboveValue = (String) settings.get(EvalSettings.INSTRUCTOR_MUST_USE_EVALS_FROM_ABOVE);
         if (instUseFromAboveValue == null) {
            UISelect.make(form, "instructorOpt", EvalToolConstants.INSTRUCTOR_OPT_VALUES, 
                  EvalToolConstants.INSTRUCTOR_OPT_LABELS, "#{evaluationBean.eval.instructorOpt}").setMessageKeys();
         } else {
            int index = ArrayUtil.indexOf(EvalToolConstants.INSTRUCTOR_OPT_VALUES, instUseFromAboveValue);
            String instUseFromAboveLabel = EvalToolConstants.INSTRUCTOR_OPT_LABELS[index];
            // Displaying the label corresponding to INSTRUCTOR_MUST_USE_EVALS_FROM_ABOVE value set as system property
            UIMessage.make(form, "instructorOptLabel", instUseFromAboveLabel);
            // Doing the binding of this INSTRUCTOR_MUST_USE_EVALS_FROM_ABOVE value so that it can be saved in the database
            form.parameters.add(new UIELBinding("#{evaluationBean.eval.instructorOpt}", instUseFromAboveValue));
         }
      }


      // EVALUATION REMINDERS SECTION

      // email available template link
      UIInternalLink.make(form, "emailAvailable_link", UIMessage.make("evalsettings.available.mail.link"), 
            new EmailViewParameters(PreviewEmailProducer.VIEW_ID, null, EvalConstants.EMAIL_TEMPLATE_AVAILABLE));

      // email reminder control
      UISelect reminderDaysSelect = UISelect.make(form, "reminderDays", EvalToolConstants.REMINDER_EMAIL_DAYS_VALUES, 
            EvalToolConstants.REMINDER_EMAIL_DAYS_LABELS, "#{evaluationBean.eval.reminderDays}").setMessageKeys();

      // email reminder template link
      UIInternalLink.make(form, "emailReminder_link", UIMessage.make("evalsettings.reminder.mail.link"), 
            new EmailViewParameters(PreviewEmailProducer.VIEW_ID, null, EvalConstants.EMAIL_TEMPLATE_REMINDER));

      // email from address control
      String defaultEmail = (String) settings.get(EvalSettings.FROM_EMAIL_ADDRESS);
      UIMessage.make(form, "eval-from-email-note", "evalsettings.email.sent.from", new String[] {defaultEmail});
      UIInput.make(form, "reminderFromEmail", "#{evaluationBean.eval.reminderFromEmail}");


      // EVALUATION EXTRAS SECTION
      Boolean categoriesEnabled = (Boolean) settings.get(EvalSettings.ENABLE_EVAL_CATEGORIES);
      if (categoriesEnabled) {
         UIBranchContainer extrasBranch = UIBranchContainer.make(form, "showEvalExtras:");

         // eval category
         if (categoriesEnabled) {
            UIBranchContainer categoryBranch = UIBranchContainer.make(extrasBranch, "showCategory:");
            UIInput.make(categoryBranch, "eval-category", "#{evaluationBean.eval.evalCategory}");
            if (evaluationBean.eval.getEvalCategory() != null) {
               UILink.make(categoryBranch, "eval-category-direct-link", UIMessage.make("general.direct.link"), 
                     externalLogic.getEntityURL(EvalCategoryEntityProvider.ENTITY_PREFIX, evaluationBean.eval.getEvalCategory()) )
                     .decorate( new UITooltipDecorator( UIMessage.make("general.direct.link.title") ) );
            }
         }
      }

      // EVAL SETTINGS SAVING CONTROLS
      // if this evaluation is already saved, show "Save Settings" button else this is the "Continue to Assign to Courses" button
      if (evaluationBean.eval.getId() == null) {
         UICommand.make(form, "continueAssigning", UIMessage.make("evalsettings.continue.assigning.link"), "#{evaluationBean.continueAssigningAction}");

         UIBranchContainer firstTime = UIBranchContainer.make(form, "firstTime:");
         UIMessage.make(firstTime, "cancel-button", "general.cancel.button");

      } else {
         UICommand.make(form, "continueAssigning", UIMessage.make("evalsettings.save.settings.link"), "#{evaluationBean.saveSettingsAction}");

         UIBranchContainer subsequentTimes = UIBranchContainer.make(form, "subsequentTimes:");
         UICommand.make(subsequentTimes, "cancel-button", UIMessage.make("general.cancel.button"), "#{evaluationBean.cancelSettingsAction}");
      }

      // this fills in the javascript call (areaId, selectId, selectValue, reminderId)
      // NOTE: RSF bug causes us to have to generate the ids manually (http://www.caret.cam.ac.uk/jira/browse/RSF-65)
      UIInitBlock.make(tofill, "initJavascript", "EvalSystem.initEvalSettings", 
            new Object[] {"evaluation_reminder_area", authControlSelect.getFullID() + "-selection", 
            EvalConstants.EVALUATION_AUTHCONTROL_NONE, reminderDaysSelect.getFullID() + "-selection"});

   }


   /* (non-Javadoc)
    * @see uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter#reportNavigationCases()
    */
   public List reportNavigationCases() {
      List i = new ArrayList();
      // Physical templateId is filled in by global Interceptor
      i.add(new NavigationCase(EvaluationStartProducer.VIEW_ID, new TemplateViewParameters(EvaluationStartProducer.VIEW_ID, null)));
      i.add(new NavigationCase(ControlEvaluationsProducer.VIEW_ID, new SimpleViewParameters(ControlEvaluationsProducer.VIEW_ID)));
      i.add(new NavigationCase(EvaluationAssignProducer.VIEW_ID, new SimpleViewParameters(EvaluationAssignProducer.VIEW_ID)));

      return i;
   }


   /**
    * This method is used to make the checkbox appear disabled whereever needed
    * @param checkbox
    */
   private void setDisabledAttribute(UIBoundBoolean checkbox) {
      checkbox.decorators = new DecoratorList(new UIDisabledDecorator());
   }

}
