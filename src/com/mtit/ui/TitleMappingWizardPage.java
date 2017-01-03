/**
 * 
 */
package com.mtit.ui;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.mtit.businessRules.TitleMappingType;

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

/**
 * @author Mei
 * 
 */
public class TitleMappingWizardPage extends WizardPage {

	class WizardPageErrorHandler implements IFieldErrorMessageHandler {

		@Override
		public void clearMessage() {
			setErrorMessage(null);
			setMessage(null, DialogPage.WARNING);
		}

		@Override
		public void handleErrorMessage(String message, String input) {
			setMessage(null, DialogPage.WARNING);
			setErrorMessage(message);
		}

		@Override
		public void handleWarningMessage(String message, String input) {
			setErrorMessage(null);
			setMessage(message, DialogPage.WARNING);
		}

	}

	private Composite container;

	private static final int DECORATOR_POSITION = SWT.TOP | SWT.LEFT;
	private static final int DECORATOR_MARGIN_WIDTH = 1;
	private static final int DEFAULT_WIDTH_HINT = 150;

	private StringValidationToolkit strValToolkit = null;

	private final IFieldErrorMessageHandler errorMessageHandler = null;

	// Validators
	private IFieldValidator<String> myobTitleValidator;
	private IFieldValidator<String> websiteDescValidator;

	// Fields
	private ValidatingField<String> myobTitleField;
	private ValidatingField<String> websiteDescField;

	// TitleMapping to set data to
	private final TitleMappingType mapType;

	public TitleMappingWizardPage(String type, TitleMappingType mapType) {
		super("Add Title Mapping");

		if ("NEW".equals(type)) {
			setTitle("Add Title Mapping");
			setDescription("Add a new title mapping");
		} else if ("EDIT".equals(type)) {
			setTitle("Edit Title");
			setDescription("Edit a title mapping");
		}
		this.mapType = mapType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// Initialize the validators
		strValToolkit = new StringValidationToolkit(DECORATOR_POSITION,
				DECORATOR_MARGIN_WIDTH, true);
		strValToolkit.setDefaultErrorMessageHandler(errorMessageHandler);

		createMyobDescField();
		createWebsiteDescField();

		setControl(container);

	}

	private void createMyobDescField() {
		Label myobLabel = new Label(container, SWT.NULL);
		myobLabel.setText("MYOB Description");

		myobTitleValidator = new IFieldValidator<String>() {

			@Override
			public String getErrorMessage() {
				return "The MYOB description field is mandatory";
			}

			@Override
			public String getWarningMessage() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isValid(String contents) {
				return !(contents.length() == 0);
			}

			@Override
			public boolean warningExist(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		myobTitleField = strValToolkit.createTextField(container,
				myobTitleValidator, true,
				mapType.getMYOBDesc()==null?"":mapType.getMYOBDesc());

		myobTitleField.getControl().setLayoutData(getGridData());
		
		myobTitleField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (myobTitleField.getContents() != null) {
					mapType.setMYOBDesc(myobTitleField.getContents());
				}

				if (!allFieldsValid()) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}

		});

	}

	private void createWebsiteDescField() {
		Label webSiteDescLabel = new Label(container, SWT.NULL);
		webSiteDescLabel.setText("WebSite Title");
		websiteDescValidator = new IFieldValidator<String>() {

			@Override
			public boolean warningExist(String contents) {
				return false;
			}

			@Override
			public boolean isValid(String contents) {
				return !(contents.length() == 0);
			}

			@Override
			public String getWarningMessage() {
				return null;
			}

			@Override
			public String getErrorMessage() {
				return "The Web Site title field is mandatory";
			}
		};

		websiteDescField = strValToolkit
				.createTextField(
						container,
						websiteDescValidator,
						true,
						mapType.getWebsiteTitle()==null?"":mapType.getWebsiteTitle());

		websiteDescField.getControl().setLayoutData(getGridData());
		websiteDescField.getControl().setToolTipText("(del to delete or any word)");
		
		
		websiteDescField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				mapType.setWebsiteTitle(websiteDescField.getContents());

				if (allFieldsValid()) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});
	}

	private GridData getGridData() {

		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = DEFAULT_WIDTH_HINT;
		return gd;
	}

	private boolean allFieldsValid() {
		if (myobTitleValidator.isValid(myobTitleField.getContents()) &&
				websiteDescValidator.isValid(websiteDescField.getContents())){
			return true;
		}
		return false;
	}
}
