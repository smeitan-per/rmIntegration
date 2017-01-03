/**
 * 
 */
package com.mtit.ui;

import java.math.BigInteger;
import java.util.Map;

import org.eclipse.jface.dialogs.DialogPage;
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
import org.mtit.businessRules.BusinessRuleType;

import com.mtit.entity.RMDao;
import com.mtit.process.SyncException;
import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.ValidationToolkit;
import com.richclientgui.toolbox.validation.converter.IntegerStringConverter;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

/**
 * @author Mei
 * 
 */
public class BusinessRuleWizardPage extends WizardPage {

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
	private String type;

	private static final int DECORATOR_POSITION = SWT.TOP | SWT.LEFT;
	private static final int DECORATOR_MARGIN_WIDTH = 1;
	private static final int DEFAULT_WIDTH_HINT = 150;

	private StringValidationToolkit strValToolkit = null;
	private ValidationToolkit<Integer> intValToolkit = null;

	private final IFieldErrorMessageHandler errorMessageHandler = null;

	// Validators
	private IFieldValidator<Integer> sequenceFieldValidator;
	private IFieldValidator<String> stockRuleValidator;
	private IFieldValidator<String> stockRangeValidator;

	// Fields
	private ValidatingField<Integer> sequenceField;
	private ValidatingField<String> stockRuleField;
	private ValidatingField<String> stockRangeField;
	
	// Combo boxes
	private Combo cat1Combo = null;
	private Combo cat2Combo = null;
	private Combo cat3Combo = null;
	
	// BusinessRuleType to set data to
	private final BusinessRuleType ruleType;

	// RetailManager DAO
	private RMDao dao;

	private Map<Integer, String[]> catMap;
	
	public BusinessRuleWizardPage(String type, BusinessRuleType ruleType) {
		super("Add Business Rule");

		this.type = type;

		if ("NEW".equals(type)) {
			setTitle("Add Business Rule");
			setDescription("Add a new business rule");
		} else if ("EDIT".equals(type)) {
			setTitle("Edit Business Rule");
			setDescription("Edit a business rule");
		}
		this.ruleType = ruleType;
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
		try {
			dao = new RMDao();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// Initialize the validators
		strValToolkit = new StringValidationToolkit(DECORATOR_POSITION,
				DECORATOR_MARGIN_WIDTH, true);
		strValToolkit.setDefaultErrorMessageHandler(errorMessageHandler);

		intValToolkit = new ValidationToolkit<Integer>(
				new IntegerStringConverter(), DECORATOR_POSITION,
				DECORATOR_MARGIN_WIDTH, true);
		intValToolkit.setDefaultErrorMessageHandler(errorMessageHandler);

		if (ruleType.getDepartment() != null) {
			try {
				catMap = dao.getCategoriesByDept(ruleType.getDepartment());
			} catch (SyncException e) {
				e.printStackTrace();
			}
		}
		createSequenceField();
		createStockRangeField();
		try {
			createDepartmentField();
			createCat1Field();
			createCat2Field();
			createCat3Field();
			createSupplierField();
			createKeywordField();
			createStockRuleField();
		} catch (SyncException e) {
			e.printStackTrace();
		}
		setControl(container);

		if ("NEW".equals(type)) {
			setPageComplete(false);
		} else {
			setPageComplete(true);
		}
	}

	private void createSequenceField() {
		Label sequenceLabel = new Label(container, SWT.NULL);
		sequenceLabel.setText("Sequence No");

		sequenceFieldValidator = new IFieldValidator<Integer>() {

			@Override
			public boolean warningExist(Integer contents) {
				return false;
			}

			@Override
			public boolean isValid(Integer contents) {
				return contents != null;
			}

			@Override
			public String getWarningMessage() {
				return null;
			}

			@Override
			public String getErrorMessage() {
				return "The sequence is a required field";
			}
		};

		sequenceField = intValToolkit.createTextField(container,
				sequenceFieldValidator, true,
				(ruleType.getSequence() == null) ? null : ruleType.getSequence()
						.intValue());

		sequenceField.getControl().setLayoutData(getGridData());

		sequenceField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (sequenceField.getContents() != null) {
					ruleType.setSequence(new BigInteger(String
							.valueOf(sequenceField.getContents())));
				}

				if (!allFieldsValid()) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}

		});

	}

	private void createStockRangeField() {
		Label stockRangeLabel = new Label(container, SWT.NULL);
		stockRangeLabel.setText("Stock Range Title");
		stockRangeValidator = new IFieldValidator<String>() {

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
				return "The stock range title is a required field";
			}
		};

		stockRangeField = strValToolkit
				.createTextField(
						container,
						stockRangeValidator,
						true,
						ruleType.getStockRangeTitle() == null ? "" : ruleType
								.getStockRangeTitle());

		stockRangeField.getControl().setLayoutData(getGridData());

		stockRangeField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ruleType.setStockRangeTitle(stockRangeField.getContents());

				if (allFieldsValid()) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

	}

	private void createDepartmentField() throws SyncException {
		Label deptLabel = new Label(container, SWT.NULL);
		deptLabel.setText("Department");

		final Combo deptCombo = new Combo(container, SWT.READ_ONLY);
		String[] deptItems = dao.getDepartments();
		deptCombo.setItems(deptItems);
		
		if (ruleType.getDepartment()==null) {
			ruleType.setDepartment("all");
		}
		deptCombo.setText(ruleType.getDepartment());

		deptCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				ruleType.setDepartment(deptCombo.getText());
				
				if ("all".equals(deptCombo.getText())) {
					cat1Combo.setText("all");
					cat2Combo.setText("all");
					cat3Combo.setText("all");
				} else {
					try {
						catMap = dao.getCategoriesByDept(deptCombo.getText());
					} catch (SyncException e1) {
						e1.printStackTrace();
					}					
					cat1Combo.setItems(catMap.get(new Integer("1")));
					cat1Combo.setText("all");
					cat1Combo.pack(true);
					
					cat2Combo.setItems(catMap.get(new Integer("2")));
					cat2Combo.setText("all");
					cat2Combo.pack(true);
					
					cat3Combo.setItems(catMap.get(new Integer("3")));
					cat3Combo.setText("all");
					cat3Combo.pack(true);
				}
			}
		});

	}

	private void createCat1Field() {
		Label cat1Label = new Label(container, SWT.NULL);
		cat1Label.setText("Category 1");
		cat1Combo = new Combo(container, SWT.READ_ONLY|SWT.BORDER);

		if (catMap != null && catMap.containsKey(new Integer("1"))) {
			cat1Combo.setItems(catMap.get(new Integer("1")));
		} else {
			cat1Combo.add("all");
		}
		
		if (ruleType.getCategory1()==null) {
			ruleType.setCategory1("all");
		}
		cat1Combo.setText(ruleType.getCategory1());

		cat1Combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				ruleType.setCategory1(cat1Combo.getText());
			}
		});

	}

	private void createCat2Field() {
		Label cat2Label = new Label(container, SWT.NULL);
		cat2Label.setText("Category 2");
		cat2Combo = new Combo(container, SWT.READ_ONLY|SWT.BORDER);
		
		if (catMap != null && catMap.containsKey(new Integer("2"))) {
			cat2Combo.setItems(catMap.get(new Integer("2")));
		} else {
			cat2Combo.add("all");
		}
		
		if (ruleType.getCategory2()==null) {
			ruleType.setCategory2("all");
		}
		cat2Combo.setText(ruleType.getCategory2());

		cat2Combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				ruleType.setCategory2(cat2Combo.getText());
			}
		});

	}

	private void createCat3Field() {
		Label cat3Label = new Label(container, SWT.NULL);
		cat3Label.setText("Category 3");
		cat3Combo = new Combo(container, SWT.READ_ONLY);

		if (catMap != null && catMap.containsKey(new Integer("3"))) {
			cat3Combo.setItems(catMap.get(new Integer("3")));
		} else {
			cat3Combo.add("all");
		}
		
		if (ruleType.getCategory3() == null ) {
			ruleType.setCategory3("all");
		}
		cat3Combo.setText(ruleType.getCategory3());

		cat3Combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				ruleType.setCategory3(cat3Combo.getText());
			}
		});

	}

	private void createSupplierField() throws SyncException {
		Label supplierLabel = new Label(container, SWT.NULL);
		supplierLabel.setText("Supplier");

		final Combo supplierCombo = new Combo(container, SWT.READ_ONLY);
		String[] supplierItems = dao.getSuppliers();
		supplierCombo.setItems(supplierItems);
		
		if (ruleType.getSupplier()==null) {
			ruleType.setSupplier("all");
		}
		supplierCombo.setText(ruleType.getSupplier());

		supplierCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				ruleType.setSupplier(supplierCombo.getText());
			}
		});

	}

	private void createKeywordField() throws SyncException {

		Label keywordLabel = new Label(container, SWT.NULL);
		keywordLabel.setText("Keyword");

		IFieldValidator<String> keywordValidator = new IFieldValidator<String>() {

			@Override
			public boolean warningExist(String contents) {
				return false;
			}

			@Override
			public boolean isValid(String contents) {
				return true;
			}

			@Override
			public String getWarningMessage() {
				return null;
			}

			@Override
			public String getErrorMessage() {
				return null;
			}
		};

		final ValidatingField<String> keywordField = strValToolkit
				.createTextField(
						container,
						keywordValidator,
						false,
						ruleType.getKeyword() == null ? "" : ruleType
								.getKeyword());

		keywordField.getControl().setLayoutData(getGridData());
		keywordField.getControl().setToolTipText("Enter keyword for matching MYOB description");
		
		keywordField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ruleType.setKeyword(keywordField.getContents());

				if (allFieldsValid()) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

	}
	
	private void createStockRuleField() {
		Label stockRuleLabel = new Label(container, SWT.NULL);
		stockRuleLabel.setText("Stock Rule");
		stockRuleValidator = new IFieldValidator<String>() {

			@Override
			public boolean warningExist(String contents) {
				return false;
			}

			@Override
			public boolean isValid(String contents) {
				boolean valid = true;
				
				if (contents.length()==0) {
					valid = false;
				}
				
				if (!("M".equalsIgnoreCase(contents) || "9999".equals(contents) || 
						isNegativeDigit(contents))) {
					valid = false;
				}
				return valid;
			}

			@Override
			public String getWarningMessage() {
				return null;
			}

			@Override
			public String getErrorMessage() {
				return "The stock rule is mandatory and should be either M, 9999 or a negative number.";
			}
			
			/**
			 * Method to help check if the content of the field stock rule is a negative digit
			 * 
			 * @param contents
			 * @return
			 */
			private boolean isNegativeDigit(String contents) {
				boolean isNegativeDigit=false;
				try {
					int stockRuleInt = Integer.valueOf(contents).intValue();
					
					if (stockRuleInt < 0 ) {
						isNegativeDigit = true;
					}
				} catch (NumberFormatException e) {
					isNegativeDigit=false;
				}
				return isNegativeDigit;
			}
		};

		stockRuleField = strValToolkit.createTextField(
				container,
				stockRuleValidator,
				true,
				(ruleType.getStockRule() == null) ? "" : ruleType
						.getStockRule());

		stockRuleField.getControl().setLayoutData(getGridData());
		stockRuleField.getControl().setToolTipText("(M, 9999 or -ve digit)");
		
		stockRuleField.getControl().addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if ("m".equals(stockRuleField.getContents())) {
					ruleType.setStockRule("M");
				} else {
					ruleType.setStockRule(stockRuleField.getContents());
				}

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
		if (sequenceFieldValidator.isValid(sequenceField.getContents()) &&
				stockRangeValidator.isValid(stockRangeField.getContents()) &&
				stockRuleValidator.isValid(stockRuleField.getContents())){
			return true;
		}
		return false;
	}
}
