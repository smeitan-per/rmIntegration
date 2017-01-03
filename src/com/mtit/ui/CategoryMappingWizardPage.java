/**
 * 
 */
package com.mtit.ui;

import java.math.BigInteger;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.entity.WebSiteCategoryDao;
import com.mtit.process.SyncException;
import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

/**
 * @author Mei
 * 
 */
public class CategoryMappingWizardPage extends WizardPage {
	
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

	private Map<String, String> websiteMap = null;
	
	private static final int DECORATOR_POSITION = SWT.TOP | SWT.LEFT;
	private static final int DECORATOR_MARGIN_WIDTH = 1;
	private static final int DEFAULT_WIDTH_HINT = 150;

	private StringValidationToolkit strValToolkit = null;

	private final IFieldErrorMessageHandler errorMessageHandler = null;

	// Validators
	private IFieldValidator<String> myobCategoryValidator;

	// Fields
	private ValidatingField<String> myobCategoryField;
	private Text websiteCatIDField;
	

	// TitleMapping to set data to
	private final CategoryMappingType mapType;

	public CategoryMappingWizardPage(String type, CategoryMappingType mapType) {
		super("Add Category Mapping");

		if ("NEW".equals(type)) {
			setTitle("Add Category Mapping");
			setDescription("Add a new category mapping");
		} else if ("EDIT".equals(type)) {
			setTitle("Edit Category Mapping");
			setDescription("Edit a Category mapping");
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

		try {
			websiteMap = WebSiteCategoryDao.getWWCategories();
			createMyobCategoryField();
			createWebsiteCatDescField();
			createWebsiteCatIDField();

		} catch (SyncException e) {
			IStatus status = new Status(IStatus.ERROR,
					e.getMessage(), e.getMessage(), e);
			ErrorDialog.openError(parent.getShell(),
					"Group File download Error Message Dialog",
					"Encountered error while downloading", status);
		}
		setControl(container);
	}

	private void createMyobCategoryField() {
		Label myobLabel = new Label(container, SWT.NULL);
		myobLabel.setText("MYOB Category");

		myobCategoryValidator = new IFieldValidator<String>() {

			@Override
			public String getErrorMessage() {
				return "The MYOB category field is mandatory";
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
		myobCategoryField = strValToolkit.createTextField(container,
				myobCategoryValidator, true,
				mapType.getMYOBCategory()==null?"":mapType.getMYOBCategory());

		myobCategoryField.getControl().setLayoutData(getGridData());

		myobCategoryField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (myobCategoryField.getContents() != null) {
					mapType.setMYOBCategory(myobCategoryField.getContents());
				}

				if (!allFieldsValid()) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}
		});
	}

	private void createWebsiteCatDescField() {
		
		Label websiteCatDescLabel = new Label(container, SWT.NULL);
		websiteCatDescLabel.setText("Website Category Name");

		final Combo websiteCatCombo = new Combo(container, SWT.READ_ONLY);
		
		String[] categoryNames = new String[websiteMap.size()];
		websiteMap.keySet().toArray(categoryNames);
		websiteCatCombo.setItems(categoryNames);
		websiteCatCombo.setText(mapType.getWebSiteCategoryName()==null?categoryNames[0]:mapType.getWebSiteCategoryName());
		websiteCatCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				mapType.setWebSiteCategoryName(websiteCatCombo.getText());
				String catID = websiteMap.get(websiteCatCombo.getText());
				websiteCatIDField.setText(catID);
				mapType.setWebSiteCategoryID(new BigInteger(catID));
			}
		});
	}

	private void createWebsiteCatIDField() {
		Label catIDLabel = new Label(container, SWT.NULL);
		catIDLabel.setText("Website Category ID");
		
		websiteCatIDField = new Text(container, SWT.None|SWT.READ_ONLY);
		websiteCatIDField.setText(mapType.getWebSiteCategoryID()==null?"":mapType.getWebSiteCategoryID().toString());
		
	}
	
	private GridData getGridData() {

		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = DEFAULT_WIDTH_HINT;
		return gd;
	}

	private boolean allFieldsValid() {
		if (myobCategoryValidator.isValid(myobCategoryField.getContents())){
			return true;
		}
		return false;
	}
}
