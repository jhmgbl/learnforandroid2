package com.jmg.learn;

import in.wptrafficanalyzer.actionbarcompatnavtabswipe.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.thinkti.android.filechooser.*;

import com.jmg.learn.chart.IDemoChart;
import com.jmg.learn.vok.*;
import com.jmg.learn.vok.Vokabel.Bewertung;
import com.jmg.learn.vok.Vokabel.EnumSprachen;
import com.jmg.lib.*;
import com.jmg.lib.ColorSetting.ColorItems;
import com.jmg.lib.lib.Sounds;
import com.jmg.lib.lib.libString;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

public class _MainActivity extends Fragment {

	private static final int FILE_CHOOSER = 34823;
	private static final int Settings_Activity = 34824;
	private static final int FILE_CHOOSERADV = 34825;
	private Context context = this.getActivity();
	private Button _btnRight;
	private Button _btnWrong;
	private Button _btnSkip;
	private Button _btnView;
	private Button _btnEdit;
	private BorderedTextView _txtWord;
	private BorderedTextView _txtKom;
	private BorderedEditText _txtedWord;
	private BorderedEditText _txtedKom;
	private BorderedTextView _txtStatus;
	private BorderedEditText _txtMeaning1;
	private BorderedEditText _txtMeaning2;
	private BorderedEditText _txtMeaning3;
	private float DisplayDurationWord;
	private float DisplayDurationBed;
	private int PaukRepetitions = 3;
	private double scale = 1;
	private boolean _blnEink;
	public HashMap<ColorItems, ColorSetting> Colors;
	public HashMap<Sounds, SoundSetting> colSounds;
	public Vokabel vok;
	public String CharsetASCII = "Windows-1252";
	public View mainView;
	public SharedPreferences prefs; // =
									// this.getPreferences(Context.MODE_PRIVATE);
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragmentactivity_main, null);
		try {
			libLearn.gStatus = "onCreate setContentView";
			//setContentView(R.layout.fragmentactivity_main);
			//mainView = findViewById(Window.ID_ANDROID_CONTENT);
			Thread.setDefaultUncaughtExceptionHandler(ErrorHandler);

			// View LayoutMain = findViewById(R.id.layoutMain);

			try {
				libLearn.gStatus = "onCreate getPrefs";
				prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
				vok = new Vokabel(getActivity(),
						(TextView) mainView.findViewById(R.id.txtStatus));
				vok.setSchrittweite((short) prefs.getInt("Schrittweite", 6));
				CharsetASCII = prefs.getString("CharsetASCII", "Windows-1252");
				vok.CharsetASCII = CharsetASCII;
				vok.setAbfragebereich((short) prefs
						.getInt("Abfragebereich", -1));
				DisplayDurationWord = prefs.getFloat("DisplayDurationWord",
						1.5f);
				DisplayDurationBed = prefs.getFloat("DisplayDurationBed", 2.5f);
				PaukRepetitions = prefs.getInt("PaukRepetitions", 3);
				vok.ProbabilityFactor = prefs
						.getFloat("ProbabilityFactor", -1f);
				vok.setAbfrageZufaellig(prefs.getBoolean("Random",
						vok.getAbfrageZufaellig()));
				vok.setAskAll(prefs.getBoolean("AskAll", vok.getAskAll()));
				lib.sndEnabled = prefs.getBoolean("Sound", lib.sndEnabled);
				Colors = getColorsFromPrefs();
				colSounds = getSoundsFromPrefs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lib.ShowException(getActivity(), e);
			}
			libLearn.gStatus = "onCreate Copy Assets";
			CopyAssets();
			try {
				libLearn.gStatus = "onCreate InitButtons";
				InitButtons();
				libLearn.gStatus = "onCreate InitMeanings";
				InitMeanings();
				String tmppath = Path.combine(getActivity().getApplicationInfo().dataDir,
						"vok.tmp");
				// SetActionBarTitle();
				boolean CardMode = false;
				if (savedInstanceState != null) {
					libLearn.gStatus = "onCreate Load SavedInstanceState";
					String filename = savedInstanceState.getString("vokpath");
					int index = savedInstanceState.getInt("vokindex");
					int[] Lernvokabeln = savedInstanceState
							.getIntArray("Lernvokabeln");
					int Lernindex = savedInstanceState.getInt("Lernindex");
					CardMode = savedInstanceState.getBoolean("Cardmode", false);
					if (!libString.IsNullOrEmpty(filename) && index > 0) {
						boolean Unicode = savedInstanceState.getBoolean(
								"Unicode", true);
						_blnUniCode = Unicode;
						LoadVokabel(tmppath, index, Lernvokabeln, Lernindex,
								CardMode);
						vok.setLastIndex(savedInstanceState.getInt(
								"vokLastIndex", vok.getLastIndex()));
						vok.setFileName(filename);
						vok.setCardMode(CardMode);
						vok.aend = savedInstanceState.getBoolean("aend", true);
						SetActionBarTitle();
					}
				} else {
					if (prefs.getString("LastFile", null) != null) {
						libLearn.gStatus = "onCreate Load Lastfile";
						String filename = prefs.getString("LastFile", "");
						int index = prefs.getInt("vokindex", 1);
						int[] Lernvokabeln = lib.getIntArrayFromPrefs(prefs,
								"Lernvokabeln");
						int Lernindex = prefs.getInt("Lernindex", 0);
						boolean Unicode = prefs.getBoolean("Unicode", true);
						_blnUniCode = Unicode;
						boolean isTmpFile = prefs
								.getBoolean("isTmpFile", false);
						boolean aend = prefs.getBoolean("aend", true);
						CardMode = prefs.getBoolean("Cardmode", false);
						if (Lernvokabeln != null) {
							if (isTmpFile) {
								LoadVokabel(tmppath, index, Lernvokabeln,
										Lernindex, CardMode);
								vok.setFileName(filename);
								vok.setCardMode(CardMode);
								vok.setLastIndex(prefs.getInt("vokLastIndex",
										vok.getLastIndex()));
								SetActionBarTitle();
								vok.aend = aend;
							} else {
								LoadVokabel(filename, index, Lernvokabeln,
										Lernindex, CardMode);
								vok.setLastIndex(prefs.getInt("vokLastIndex",
										vok.getLastIndex()));
							}
						} else {
							if (isTmpFile) {
								LoadVokabel(tmppath, 1, null, 0, CardMode);
								vok.setFileName(filename);
								vok.setCardMode(CardMode);
								SetActionBarTitle();
								vok.aend = aend;
							} else {
								LoadVokabel(filename, 1, null, 0, CardMode);
							}
						}

					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				lib.ShowException(getActivity(), e);
			}
			if (true)
			{
				mainView.getViewTreeObserver().addOnGlobalLayoutListener(
						new ViewTreeObserver.OnGlobalLayoutListener() {

							@Override
							public void onGlobalLayout() {
								// Ensure you call it only once :
								lib.removeLayoutListener(mainView.getViewTreeObserver(), this);
								// Here you can get the size :)
								resize();
								//lib.ShowToast(SettingsActivity.this, "Resize End");
							}
						});

			}
			

		} catch (Exception ex) {
			lib.ShowException(getActivity(), ex);
		}

		
		return mainView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	public UncaughtExceptionHandler ErrorHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// TODO Auto-generated method stub
			lib.ShowException(getActivity(), ex);
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			boolean aend = vok.aend;
			String filename = vok.getFileName();
			if (vok.getGesamtzahl() > 0 && !libString.IsNullOrEmpty(filename)) {
				saveFilePrefs(true);
				vok.SaveFile(
						Path.combine(getActivity().getApplicationInfo().dataDir, "vok.tmp"),
						vok.getUniCode(), true);
				outState.putString("vokpath", filename);
				outState.putInt("vokindex", vok.getIndex());
				outState.putInt("vokLastIndex", vok.getLastIndex());
				outState.putIntArray("Lernvokabeln", vok.getLernvokabeln());
				outState.putInt("Lernindex", vok.getLernIndex());
				outState.putBoolean("Unicode", vok.getUniCode());
				outState.putBoolean("Cardmode", vok.getCardMode());
				outState.putBoolean("aend", aend);
				vok.aend = aend;
				vok.setFileName(filename);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("OnSaveInstanceState", e.getMessage(), e);
			e.printStackTrace();
		}
		// outState.putParcelable("vok", vok);

	}

	
	/*
	 * @Override public void onAttachedToWindow() {
	 * super.onAtFILE_CHOOSERtachedToWindow();
	 * this.getWindow().setType(WindowManager
	 * .LayoutParams.TYPE_KEYGUARD_DIALOG); }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_HOME) {
			try {
				saveVok(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lib.ShowException(this, e);
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				if (_backPressed > 0 || saveVokAsync(false)) {
					handlerbackpressed.removeCallbacks(rSetBackPressedFalse);
				} else {
					return true;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("onBackPressed", e.getMessage(), e);
				lib.ShowException(this, e);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);

	};

	private int _backPressed;
	private Handler handlerbackpressed = new Handler();

	private synchronized boolean saveVok(boolean dontPrompt) throws Exception {
		EndEdit();
		if (vok.aend) {
			if (!dontPrompt) {
				dontPrompt = lib.ShowMessageYesNo(this,
						getString(R.string.Save));
				if (!dontPrompt) {
					_backPressed += 1;
					lib.ShowToast(_MainActivity.this, _MainActivity.this
							.getString(R.string.PressBackAgain));
					handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);

				}
				/*
				 * AlertDialog.Builder A = new AlertDialog.Builder(context);
				 * A.setPositiveButton(getString(R.string.yes), new
				 * AlertDialog.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { try { vok.SaveFile(vok.getFileName(),
				 * vok.getUniCode()); vok.aend = false; _backPressed += 1;
				 * Handler handler = new Handler();
				 * handler.postDelayed(rSetBackPressedFalse, 10000);
				 * saveFilePrefs(); } catch (Exception e) { // TODO
				 * Auto-generated catch block
				 * lib.ShowException(MainActivity.this, e); } } });
				 * A.setNegativeButton(getString(R.string.no), new
				 * AlertDialog.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { lib.ShowToast( MainActivity.this, MainActivity.this
				 * .getString(R.string.PressBackAgain)); _backPressed += 1;
				 * Handler handler = new Handler();
				 * handler.postDelayed(rSetBackPressedFalse, 10000); }
				 * 
				 * }); A.setMessage(getString(R.string.Save));
				 * A.setTitle("Question"); A.show(); if (!dontPrompt) { if
				 * (_backPressed > 0) { return true; } else {
				 * lib.ShowToast(this, this.getString(R.string.PressBackAgain));
				 * _backPressed += 1; handler = new Handler();
				 * handler.postDelayed(rSetBackPressedFalse, 10000); }
				 * 
				 * }
				 */
			}

			if (dontPrompt) {
				try {
					if (libString.IsNullOrEmpty(vok.getFileName()))
					{
						SaveVokAs(true);
					}
					else
					{
						vok.SaveFile();
						vok.aend = false;
						_backPressed += 1;
						handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
						saveFilePrefs(false);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(this, e);
				}
			}
			return false;
		} else {
			return true;
		}

	}

	private boolean saveVokAsync(boolean dontPrompt) throws Exception {
		EndEdit();
		if (vok.aend) {
			if (!dontPrompt) {
				
				 AlertDialog.Builder A = new AlertDialog.Builder(context);
				 A.setPositiveButton(getString(R.string.yes), new
				 AlertDialog.OnClickListener() 
				 {				  
				  @Override public void onClick(DialogInterface dialog, int
				  which) 
				  { 
					  try 
					  { 
						  if (libString.IsNullOrEmpty(vok.getFileName()))
						  {
							  SaveVokAs(true);
						  }
						  else
						  {
							  vok.SaveFile(vok.getFileName(), vok.getUniCode(),	false); 
							  vok.aend = false; 
							  _backPressed += 1;
							  handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
							  saveFilePrefs(false);  
						  }
						   
					  } 
					  catch (Exception e) 
					  { 
						  lib.ShowException(_MainActivity.this, e); 
						  } 
					  } 
				  });
				  A.setNegativeButton(getString(R.string.no), new
				  AlertDialog.OnClickListener() 
				  {
					  @Override 
					  public void onClick(DialogInterface dialog, int which) 
					  { 
						  lib.ShowToast( _MainActivity.this, 
								  _MainActivity.this.getString(R.string.PressBackAgain)); 
						  _backPressed += 1;
						  handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000); 
					  }
				  }); 
				  A.setMessage(getString(R.string.Save));
				  A.setTitle("Question"); 
				  A.show(); 
				  if (!dontPrompt) 
				  { 
					  if (_backPressed > 0) 
					  { 
						  return true; 
					  } 
					  else 
					  {
						  lib.ShowToast(this, this.getString(R.string.PressBackAgain));
						  _backPressed += 1; 
						  handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000); }
				  
				  }
				 
			}

			if (dontPrompt) {
				try {
					if (libString.IsNullOrEmpty(vok.getFileName()))
					{
						SaveVokAs(true);
					}
					else
					{
						vok.SaveFile();
						vok.aend = false;
						_backPressed += 1;
						handlerbackpressed.postDelayed(rSetBackPressedFalse, 10000);
						saveFilePrefs(false);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(this, e);
				}
			}
			return false;
		} else {
			return true;
		}

	}

	private void saveFilePrefs(boolean isTmpFile) {
		Editor edit = prefs.edit();
		edit.putString("LastFile", vok.getFileName()).commit();
		edit.putInt("vokindex", vok.getIndex());
		edit.putInt("vokLastIndex", vok.getLastIndex());
		lib.putIntArrayToPrefs(prefs, vok.getLernvokabeln(), "Lernvokabeln");
		edit.putInt("Lernindex", vok.getLernIndex());
		edit.putBoolean("Unicode", vok.getUniCode());
		edit.putBoolean("isTmpFile", isTmpFile);
		edit.putBoolean("Cardmode", vok.getCardMode());
		edit.putBoolean("aend", vok.aend);
		edit.commit();
	}

	private Runnable rSetBackPressedFalse = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			_backPressed = 0;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * if (_firstFocus) { _firstFocus = false; hideKeyboard(); }
		 */
		// resize();
	}

	@Override
	protected void onPause() {
		// saveVok(false);
		super.onPause();
		/*
		 * if (_firstFocus) { _firstFocus = false; hideKeyboard(); }
		 */
		// resize();
	}

	@Override
	protected void onStart() {
		super.onStart();
		/*
		 * if (_firstFocus) { _firstFocus = false; hideKeyboard(); }
		 */
		// resize();
	}

	private int _lastIsWrongVokID;

	private void InitButtons() throws Exception {
		View v = findViewById(R.id.btnRight);
		Button b = (Button) v;
		_btnRight = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (_lastIsWrongVokID == vok.getIndex()) {
						lib.playSound(_MainActivity.this, Sounds.Beep);
						getVokabel(false, true);
					} else {

						int Zaehler = vok.AntwortRichtig();
						lib.playSound(_MainActivity.this, Zaehler);

						getVokabel(false, false);
					}
					_lastIsWrongVokID = -1;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(_MainActivity.this, e);

				}

			}
		});
		v = findViewById(R.id.btnWrong);
		b = (Button) v;
		_btnWrong = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					vok.AntwortFalsch();
					lib.playSound(_MainActivity.this, vok.getZaehler());
					_lastIsWrongVokID = vok.getIndex();

					if (!vok.getCardMode()) {
						setBtnsEnabled(false);
						flashwords();
						// getVokabel(false,true);
						// runFlashWords();
						Handler handler = new Handler();
						handler.postDelayed(
								runnableFalse,
								(long) ((DisplayDurationWord * 1000 + vok
										.getAnzBed()
										* 1000
										* DisplayDurationBed) * PaukRepetitions));
					} else {
						getVokabel(false, false);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(_MainActivity.this, e);
				}

			}
		});
		v = findViewById(R.id.btnSkip);
		b = (Button) v;
		_btnSkip = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					vok.SkipVokabel();
					getVokabel(false, false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(_MainActivity.this, e);
				}

			}
		});
		v = findViewById(R.id.btnView);
		b = (Button) v;
		_btnView = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					getVokabel(true, false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(_MainActivity.this, e);
				}

			}
		});

		v = findViewById(R.id.btnEdit);
		b = (Button) v;
		_btnEdit = b;
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (_txtWord.getVisibility()==View.VISIBLE)
					{
						StartEdit();
					}
					else
					{
						EndEdit();
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lib.ShowException(_MainActivity.this, e);
				}

			}
		});

		_txtMeaning1 = (BorderedEditText) findViewById(R.id.txtMeaning1);
		_txtMeaning1.setBackgroundResource(0);
		_txtMeaning2 = (BorderedEditText) findViewById(R.id.txtMeaning2);
		_txtMeaning2.setBackgroundResource(0);
		_txtMeaning3 = (BorderedEditText) findViewById(R.id.txtMeaning3);
		_txtMeaning3.setBackgroundResource(0);
		_txtWord = (BorderedTextView) findViewById(R.id.word);
		_txtedWord= (BorderedEditText) findViewById(R.id.edword);
		_txtedKom = (BorderedEditText) findViewById(R.id.edComment);
		_txtKom = (BorderedTextView) findViewById(R.id.Comment);
		_txtStatus = (BorderedTextView) findViewById(R.id.txtStatus);
		setBtnsEnabled(false);
		setTextColors();
	}
	
	private void StartEdit() throws Exception
	{
		_txtWord.setVisibility(View.GONE);
		_txtKom.setVisibility(View.GONE);
		_txtedWord.setVisibility(View.VISIBLE);
		_txtedWord.setText(_txtWord.getText());
		_txtedWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,_txtWord.getTextSize());
		_txtedKom.setVisibility(View.VISIBLE);
		_txtedKom.setText(_txtKom.getText());
		_txtedKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,_txtKom.getTextSize());
		_txtedWord.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		_txtedKom.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		if (!vok.getCardMode())
		{
			_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			_txtMeaning2.setVisibility(View.VISIBLE);
			_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			_txtMeaning3.setVisibility(View.VISIBLE);
			_txtMeaning3.setImeOptions(EditorInfo.IME_ACTION_DONE);
			_txtMeaning2.setText(vok.getBedeutung2());
			_txtMeaning3.setText(vok.getBedeutung3());
		}
		else
		{
			_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_DONE);
		}
		_txtMeaning1.setText(vok.getBedeutung1());
		_txtedWord.requestFocus();
		setBtnsEnabled(false);
		_btnEdit.setEnabled(true);
		
	}
	
	private void EndEdit() throws Exception
	{
		if (_txtedWord.getVisibility()== View.VISIBLE)
		{
			_txtWord.setVisibility(View.VISIBLE);
			_txtKom.setVisibility(View.VISIBLE);
			_txtedWord.setVisibility(View.GONE);
			_txtWord.setText(_txtedWord.getText());
			_txtedKom.setVisibility(View.GONE);
			_txtKom.setText(_txtedKom.getText());
			_txtedWord.setImeOptions(EditorInfo.IME_ACTION_NONE);
			_txtedKom.setImeOptions(EditorInfo.IME_ACTION_NONE);
			_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_NONE);
			_txtMeaning2.setVisibility(View.VISIBLE);
			_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_NONE);
			_txtMeaning3.setVisibility(View.VISIBLE);
			_txtMeaning3.setImeOptions(EditorInfo.IME_ACTION_NONE);
			vok.setWort(_txtedWord.getText().toString());
			vok.setKommentar(_txtedKom.getText().toString());
			vok.setBedeutung1(_txtMeaning1.getText().toString());
			vok.setBedeutung2(_txtMeaning2.getText().toString());
			vok.setBedeutung3(_txtMeaning3.getText().toString());
			getVokabel(false, false);
		}
		
	}

	private void InitMeanings() {
		_txtMeaning1.setOnEditorActionListener(EditorActionListener);
		_txtMeaning2.setOnEditorActionListener(EditorActionListener);
		_txtMeaning3.setOnEditorActionListener(EditorActionListener);
	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private OnEditorActionListener EditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			if (event == null) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					hideKeyboard();
					String[] Antworten;
					com.jmg.learn.vok.Vokabel.Bewertung Bew;
					String meaning1 = _txtMeaning1.getText().toString();
					String meaning2 = _txtMeaning2.getVisibility() == View.VISIBLE ? _txtMeaning2
							.getText().toString() : "";
					String meaning3 = _txtMeaning3.getVisibility() == View.VISIBLE ? _txtMeaning3
							.getText().toString() : "";
					if (_txtWord.getVisibility()==View.VISIBLE)
					{
						Antworten = new String[] { meaning1, meaning2, meaning3 };
						try {
							Bew = vok.CheckAntwort(Antworten);
							if (Bew == Bewertung.AllesRichtig) {
								lib.ShowToast(_MainActivity.this,
										getString(R.string.AnswerCorrect));
								_btnRight.performClick();
							} else if (Bew == Bewertung.AllesFalsch) {
								try {
									vok.AntwortFalsch();
									lib.playSound(_MainActivity.this,
											vok.getZaehler());
									_lastIsWrongVokID = vok.getIndex();
									getVokabel(true, false);
									if (!vok.getCardMode()) {
										setBtnsEnabled(false);
										flashwords();
										Handler handler = new Handler();
										handler.postDelayed(
												runnableFalse,
												(long) ((DisplayDurationWord * 1000 + vok
														.getAnzBed()
														* 1000
														* DisplayDurationBed) * PaukRepetitions));
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									lib.ShowException(_MainActivity.this, e);
								}
							} else if (Bew == Bewertung.aehnlich) {
								lib.ShowMessage(_MainActivity.this,
										getString(R.string.MeaningSimilar));
							} else if (Bew == Bewertung.TeilweiseRichtig) {
								lib.ShowMessage(_MainActivity.this,
										getString(R.string.MeaningPartiallyCorrect));
							} else if (Bew == Bewertung.enthalten) {
								lib.ShowMessage(_MainActivity.this,
										getString(R.string.MeaningIsSubstring));
							} else if (Bew == Bewertung.AehnlichEnthalten) {
								lib.ShowMessage(
										_MainActivity.this,
										getString(R.string.MeaningIsSubstringSimilar));
							} else if (Bew == Bewertung.TeilweiseRichtigAehnlich) {
								lib.ShowMessage(
										_MainActivity.this,
										getString(R.string.MeaningIsPartiallyCorrectSimilar));
							} else if (Bew == Bewertung.TeilweiseRichtigAehnlichEnthalten) {
								lib.ShowMessage(
										_MainActivity.this,
										getString(R.string.MeaningIsPartiallyCorrectSimilarSubstring));
							} else if (Bew == Bewertung.TeilweiseRichtigEnthalten) {
								lib.ShowMessage(
										_MainActivity.this,
										getString(R.string.MeaningIsPartiallyCorrectSubstring));
							}
	
						} catch (Exception e) {
							// TODO Auto-generated catch block
							lib.ShowException(_MainActivity.this, e);
						}
					}
					else
					{
						try {
							EndEdit();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							lib.ShowException(_MainActivity.this, e);
						}
						
					}
					return true;
				}
				// Capture soft enters in a singleLine EditText that is the last
				// EditText.
				else if (actionId == EditorInfo.IME_ACTION_NEXT)
					return false;
				// Capture soft enters in other singleLine EditTexts

				else
					return false; // Let system handle all other null KeyEvents

			} else if (actionId == EditorInfo.IME_NULL) {
				// Capture most soft enters in multi-line EditTexts and all hard
				// enters.
				// They supply a zero actionId and a valid KeyEvent rather than
				// a non-zero actionId and a null event like the previous cases.
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					return false;
				// We capture the event when key is first pressed.
				else
					return false; // We consume the event when the key is
									// released.
			} else
				return false;
		}
	};

	private void setTextColors() {
		libLearn.gStatus = "setTextColors";
		_txtMeaning1.setTextColor(Colors.get(ColorItems.meaning).ColorValue);
		_txtMeaning2.setTextColor(Colors.get(ColorItems.meaning).ColorValue);
		_txtMeaning3.setTextColor(Colors.get(ColorItems.meaning).ColorValue);
		_txtWord.setTextColor(Colors.get(ColorItems.word).ColorValue);
		_txtKom.setTextColor(Colors.get(ColorItems.comment).ColorValue);
		/*
		 * _txtMeaning1.setBackgroundColor(Colors.get(ColorItems.background).
		 * ColorValue);
		 * _txtMeaning2.setBackgroundColor(Colors.get(ColorItems.background
		 * ).ColorValue);
		 * _txtMeaning3.setBackgroundColor(Colors.get(ColorItems.background
		 * ).ColorValue);
		 * _txtWord.setBackgroundColor(Colors.get(ColorItems.background
		 * ).ColorValue);
		 * _txtKom.setBackgroundColor(Colors.get(ColorItems.background
		 * ).ColorValue);
		 */
		findViewById(R.id.layoutMain).setBackgroundColor(
				Colors.get(ColorItems.background).ColorValue);
	}

	private void setBtnsEnabled(boolean enable) {
		libLearn.gStatus = "setBtnsEnabled";
		_btnEdit.setEnabled(enable);
		_btnRight.setEnabled(enable);
		_btnSkip.setEnabled(enable);
		_btnView.setEnabled(enable);
		_btnWrong.setEnabled(enable);

	}

	/*
	 * private Runnable runnableGetVok = new Runnable() {
	 * 
	 * @Override public void run() { // do what you need to do
	 * getVokabel(false,true); } };
	 */
	private Runnable runnableFalse = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			getVokabel(false, false);
		}
	};

	/*
	 * private void runFlashWords() { new Thread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub try {
	 * flashwords(); } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } }).start(); }
	 */
	private void flashwords() throws Exception {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layoutMain);
		layout.setBackgroundColor(Colors.get(ColorItems.background_wrong).ColorValue);
		Handler handler = new Handler();
		long delay = 0;
		for (int i = 0; i < PaukRepetitions; i++) {
			// _txtWord.setBackgroundResource(R.layout.roundedbox);
			handler.postDelayed(new showWordBordersTask(), delay);
			delay += DisplayDurationWord * 1000;
			handler.postDelayed(new hideWordBordersTask(), delay);
			BorderedEditText Beds[] = { _txtMeaning1, _txtMeaning2,
					_txtMeaning3 };
			for (int ii = 0; ii < vok.getAnzBed(); ii++) {
				handler.postDelayed(new showBedBordersTask(Beds[ii]), delay);
				delay += DisplayDurationBed * 1000;
				handler.postDelayed(new hideBedBordersTask(Beds[ii]), delay);
			}

		}
		handler.postDelayed(new resetLayoutTask(layout), delay);
		delay += 1000;

	}

	private class resetLayoutTask implements Runnable {
		public RelativeLayout layout;

		public resetLayoutTask(RelativeLayout layout) {
			// TODO Auto-generated constructor stub
			this.layout = layout;
		}

		public void run() {
			layout.setBackgroundColor(Colors.get(ColorItems.background).ColorValue);
		}
	}

	private class showWordBordersTask implements Runnable {
		public void run() {
			try {
				lib.playSound(_MainActivity.this, com.jmg.lib.lib.Sounds.Beep);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showWordBorders();
		}
	}

	private class hideWordBordersTask implements Runnable {

		public void run() {
			// TODO Auto-generated method stub
			hideWordBorders();
		}

	}

	private class showBedBordersTask implements Runnable {
		public BorderedEditText Bed;

		public showBedBordersTask(BorderedEditText Bed) {
			// TODO Auto-generated constructor stub
			this.Bed = Bed;
		}

		public void run() {
			// TODO Auto-generated method stub
			// Bed.setPadding(5, 5, 5, 5);
			Bed.setShowBorders(true,
					Colors.get(ColorItems.box_meaning).ColorValue);
			try {
				lib.playSound(_MainActivity.this, com.jmg.lib.lib.Sounds.Beep);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class hideBedBordersTask implements Runnable {
		public BorderedEditText Bed;

		public hideBedBordersTask(BorderedEditText Bed) {
			// TODO Auto-generated constructor stub
			this.Bed = Bed;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Bed.setPadding(0, 0, 0, 0);
			Bed.setShowBorders(false,
					Colors.get(ColorItems.background).ColorValue);
		}

	}

	/*
	 * private class CancelTimerTask extends TimerTask { public Timer T; public
	 * CancelTimerTask(Timer T) { // TODO Auto-generated constructor stub this.T
	 * = T; }
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * T.cancel(); }
	 * 
	 * }
	 */
	private void showWordBorders() {
		// TODO Auto-generated method stub
		// _txtWord.setPadding(5, 5, 5, 5);
		_txtWord.setShowBorders(true,
				Colors.get(ColorItems.box_word).ColorValue);
	}

	private void hideWordBorders() {
		// TODO Auto-generated method stub
		// _txtWord.setPadding(0, 0, 0, 0);
		_txtWord.setShowBorders(false,
				Colors.get(ColorItems.background).ColorValue);
	}

	private boolean _firstFocus = true;

	private void resize() {
		// _firstFocus = true;
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		int viewTop = findViewById(Window.ID_ANDROID_CONTENT).getTop();
		height = height - viewTop;
		scale = (double) height / (double) 950;
		/*
		 * lib.ShowMessage(this, "Meaning3 Bottom: " +_txtMeaning3.getBottom() +
		 * "\nbtnRight.Top: " + _btnRight.getTop() + "\nDisplayHeight: " +
		 * height);
		 */
		if (scale != 1) {

			resizeActionbar(0);
			

			lib.ShowToast(this, "Scaling font by " + scale + " Screenheight = "
					+ height);
			_txtMeaning1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtMeaning1.getTextSize() * scale));
			_txtMeaning2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtMeaning2.getTextSize() * scale));
			_txtMeaning3.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtMeaning3.getTextSize() * scale));
			_txtWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtWord.getTextSize() * scale));
			_txtKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtKom.getTextSize() * scale));
			_txtedWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtedWord.getTextSize() * scale));
			_txtedKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtedKom.getTextSize() * scale));
			_txtStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_txtStatus.getTextSize() * scale));

			/*
			 * _txtMeaning1.setOnFocusChangeListener(new
			 * View.OnFocusChangeListener() {
			 * 
			 * @Override public void onFocusChange(View v, boolean hasFocus) {
			 * // TODO Auto-generated method stub if (_firstFocus && hasFocus) {
			 * hideKeyboard(); _firstFocus = false; } } });
			 */

			_btnRight.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnRight.getTextSize() * scale));
			_btnSkip.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnSkip.getTextSize() * scale));
			_btnView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnView.getTextSize() * scale));
			_btnWrong.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnWrong.getTextSize() * scale));
			_btnEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					(float) (_btnEdit.getTextSize() * scale));

			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) _txtMeaning1
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			_txtMeaning1.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _txtMeaning2
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			_txtMeaning2.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _txtMeaning3
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			_txtMeaning3.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _btnRight
					.getLayoutParams();
			params.height = (int) (params.height * scale);
			params.width = (int) (params.width * scale);
			_btnRight.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _btnWrong
					.getLayoutParams();
			params.height = (int) (params.height * scale);
			params.width = (int) (params.width * scale);
			_btnWrong.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _btnSkip
					.getLayoutParams();
			params.height = (int) (params.height * scale);
			params.width = (int) (params.width * scale);
			_btnSkip.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _btnView
					.getLayoutParams();
			params.height = (int) (params.height * scale);
			params.width = (int) (params.width * scale);
			_btnView.setLayoutParams(params);
			params = (android.widget.RelativeLayout.LayoutParams) _btnEdit
					.getLayoutParams();
			params.height = (int) (params.height * scale);
			params.width = (int) (params.width * scale);
			_btnEdit.setLayoutParams(params);

		}
	}

	public void resizeActionbar(int width) {
		View tb = this.findViewById(R.id.action_bar);
		Paint p = new Paint();
		int SizeOther = 0;
		if (tb != null) {
			if (width == 0)
				width = tb.getWidth();
			if (width > 0) {
				ViewGroup g = (ViewGroup) tb;
				for (int i = 0; i < g.getChildCount(); i++) {
					View v = g.getChildAt(i);
					if (!(v instanceof TextView)) {
						SizeOther += v.getWidth();
					}
				}
				if (SizeOther == 0) SizeOther=lib.dpToPx(50);
				for (int i = 0; i < g.getChildCount(); i++) {
					View v = g.getChildAt(i);
					if (v instanceof TextView) {
						TextView t = (TextView) v;

						if (t.getText() instanceof SpannedString) {
							p.setTextSize(t.getTextSize());
							SpannedString s = (SpannedString) t.getText();
							width = width - SizeOther - lib.dpToPx(50);
							float measuredWidth = p.measureText(s.toString());
							if (measuredWidth > width)
							{
								t.setTextSize(
										TypedValue.COMPLEX_UNIT_PX,
										(float) (t.getTextSize() * (width / measuredWidth)));
							}
							
						}
					}
				}

			}

		}
	}

	public String JMGDataDirectory;

	private void CopyAssets() {
		libLearn.gStatus = "Copy Assets";
		File F = android.os.Environment.getExternalStorageDirectory();
		String extPath = F.getPath();
		JMGDataDirectory = Path.combine(extPath, "learnforandroid", "vok");

		if (F.isDirectory() && F.exists()) {
			File F1 = new File(JMGDataDirectory);
			if (F1.isDirectory() == false && !F1.exists()) {
				F1.mkdirs();
			}
			AssetManager A = this.getAssets();
			try {
				final String languages[] = new String[] { "Greek", "Hebrew",
						"KAR", "Spanish" };
				final String path = F1.getPath();
				for (String language : languages) {
					F1 = new File(Path.combine(path, language));
					for (String File : A.list(language)) {
						InputStream myInput = A.open(Path.combine(language,
								File));
						String outFileName = Path.combine(F1.getPath(), File);
						if (F1.isDirectory() == false) {
							F1.mkdirs();
						}
						// Open the empty db as the output stream

						File file = new File(outFileName);

						if (file.exists()) {
							// file.delete();
						} else {
							file.createNewFile();
							OutputStream myOutput = new FileOutputStream(file);

							byte[] buffer = new byte[1024];
							int length;
							while ((length = myInput.read(buffer, 0, 1024)) > 0) {
								myOutput.write(buffer, 0, length);
							}

							// Close the streams
							myOutput.flush();
							myOutput.close();
							myInput.close();
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				lib.ShowException(this, e);
				// lib.ShowMessage(this, "CopyAssets");
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		try {
			getMenuInflater().inflate(R.menu.main, menu);
			//resize();
			return true;
		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		try {
			if (id == R.id.action_settings) {
				ShowSettings();
			} else if (id == R.id.mnuFileOpen) {
				LoadFile(true);
			} else if (id == R.id.mnuNew) {
				saveVok(false);
				vok.NewFile();
				if (lib.ShowMessageYesNo(this, getString(R.string.txtFlashCardFile)))
				{
					vok.setCardMode(true);
				}
				vok.AddVokabel();
				getVokabel(true, false);
				StartEdit();
			} else if (id == R.id.mnuAddWord) {
				vok.AddVokabel();
				getVokabel(true, false);
				StartEdit();
			} else if (id == R.id.mnuFileOpenASCII) {
				LoadFile(false);
			} else if (id == R.id.mnuConvMulti) {
				vok.ConvertMulti();
				getVokabel(false, false);
			} else if (id == R.id.mnuFileSave) {
				saveVok(false);
			} else if (id == R.id.mnuSaveAs) {
				SaveVokAs(true);
			} else if (id == R.id.mnuRestart) {
				vok.restart();
			} else if (id == R.id.mnuDelete) {
				vok.DeleteVokabel();
				getVokabel(false, false);
			} else if (id == R.id.mnuReverse) {
				vok.revert();
				getVokabel(false, false);
			} else if (id == R.id.mnuReset) {
				if (lib.ShowMessageYesNo(this,
						this.getString(R.string.ResetVocabulary))) {
					vok.reset();
				}

			} else if (id == R.id.mnuStatistics) {
				if (vok.getGesamtzahl() > 5) {
					try {
						IDemoChart chart = new com.jmg.learn.chart.LearnBarChart();
						Intent intent = chart.execute(this);
						this.startActivity(intent);
					} catch (Exception ex) {
						lib.ShowException(this, ex);
					}

				}
			}

		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}
		return super.onOptionsItemSelected(item);
	}

	public void SaveVokAs(boolean blnUniCode) throws Exception {
		EndEdit();
		Intent intent = new Intent(this, AdvFileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".k??");
		extensions.add(".v??");
		extensions.add(".K??");
		extensions.add(".V??");
		extensions.add(".KAR");
		extensions.add(".VOK");
		extensions.add(".kar");
		extensions.add(".vok");
		extensions.add(".dic");
		extensions.add(".DIC");

		intent.putStringArrayListExtra("filterFileExtension", extensions);
		intent.putExtra("blnUniCode", blnUniCode);
		intent.putExtra("DefaultDir",
				new File(JMGDataDirectory).exists() ? JMGDataDirectory
						: "/sdcard/");
		intent.putExtra("selectFolder", true);
		if (_blnUniCode)
			_oldUnidCode = yesnoundefined.yes;
		else
			_oldUnidCode = yesnoundefined.no;
		_blnUniCode = blnUniCode;

		this.startActivityForResult(intent, FILE_CHOOSERADV);
	}

	public void LoadFile(boolean blnUniCode) {
		Intent intent = new Intent(this, FileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".k??");
		extensions.add(".v??");
		extensions.add(".K??");
		extensions.add(".V??");
		extensions.add(".KAR");
		extensions.add(".VOK");
		extensions.add(".kar");
		extensions.add(".vok");
		extensions.add(".dic");
		extensions.add(".DIC");

		intent.putStringArrayListExtra("filterFileExtension", extensions);
		intent.putExtra("blnUniCode", blnUniCode);
		intent.putExtra("DefaultDir",
				new File(JMGDataDirectory).exists() ? JMGDataDirectory
						: "/sdcard/");
		if (_blnUniCode)
			_oldUnidCode = yesnoundefined.yes;
		else
			_oldUnidCode = yesnoundefined.no;
		_blnUniCode = blnUniCode;

		this.startActivityForResult(intent, FILE_CHOOSER);
	}

	public void ShowSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		intent.putExtra("Abfragebereich", vok.getAbfragebereich());
		intent.putExtra("CharsetASCII", vok.CharsetASCII);
		intent.putExtra("Step", vok.getSchrittweite());
		intent.putExtra("DisplayDurationWord", DisplayDurationWord);
		intent.putExtra("DisplayDurationBed", DisplayDurationBed);
		intent.putExtra("PaukRepetitions", PaukRepetitions);
		float ProbFact = vok.ProbabilityFactor;
		intent.putExtra("ProbabilityFactor", ProbFact);
		intent.putExtra("Random", vok.getAbfrageZufaellig());
		intent.putExtra("AskAll", vok.getAskAll());
		intent.putExtra("Sound", lib.sndEnabled);
		intent.putExtra("Language", vok.getSprache().ordinal());
		this.startActivityForResult(intent, Settings_Activity);
	}

	boolean _blnUniCode = true;
	yesnoundefined _oldUnidCode = yesnoundefined.undefined;

	enum yesnoundefined {
		yes, no, undefined
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if ((requestCode == FILE_CHOOSER)
					&& (resultCode == Activity.RESULT_OK)) {
				String fileSelected = data.getStringExtra("fileSelected");
				_blnUniCode = data.getBooleanExtra("blnUniCode", true);
				LoadVokabel(fileSelected, 1, null, 0, false);

			}
			if ((requestCode == FILE_CHOOSERADV)
					&& (resultCode == Activity.RESULT_OK)) {
				final String fileSelected = data.getStringExtra("fileSelected");
				_blnUniCode = data.getBooleanExtra("blnUniCode", true);
				if (!libString.IsNullOrEmpty(fileSelected)) {
					AlertDialog.Builder alert = new AlertDialog.Builder(this);

					alert.setTitle(getString(R.string.SaveAs));
					alert.setMessage(getString(R.string.EnterNewFilename)
							+ ": " + fileSelected);

					// Set an EditText view to get user input
					final EditText input = new EditText(this);
					alert.setView(input);
					input.setText(new File(vok.getFileName()).getName());
					alert.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String value = input.getText().toString();
									try {
										if (vok.getCardMode())
										{
											if (!lib.ExtensionMatch(value, "k??"))
											{
												value += ".kar";
											}
										}
										else
										{
											if (!lib.ExtensionMatch(value, "v??"))
											{
												value += ".vok";
											}
										}
										File F = new File(Path.combine(
												fileSelected, value));
										if (!F.isDirectory()
												&& (!F.exists() || lib
														.ShowMessageYesNo(
																_MainActivity.this,
																getString(R.string.Overwrite)))) {
											File ParentDir = F.getParentFile();
											if (!ParentDir.exists())
												ParentDir.mkdirs();
											
											vok.SaveFile(F.getPath(),
													_blnUniCode, false);
											saveFilePrefs(false);
											SetActionBarTitle();
										}

									} catch (Exception e) {
										// TODO Auto-generated catch block
										lib.ShowException(_MainActivity.this, e);
										e.printStackTrace();
									}
								}
							});

					alert.setNegativeButton(getString(R.string.btnCancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Canceled.
								}
							});

					alert.show();

				}

			}

			else if ((requestCode == Settings_Activity)
					&& (resultCode == Activity.RESULT_OK)) {
				libLearn.gStatus = "getting values from intent";
				int oldAbfrage = vok.getAbfragebereich();
				vok.setAbfragebereich(data.getExtras().getShort(
						"Abfragebereich"));
				if (oldAbfrage != vok.getAbfragebereich())
					vok.ResetAbfrage();
				short Schrittweite = data.getExtras().getShort("Step");
				if (Schrittweite != vok.getSchrittweite()) {
					vok.setSchrittweite(Schrittweite);
					vok.InitAbfrage();
				}
				vok.CharsetASCII = (data.getExtras().getString("CharsetASCII"));
				DisplayDurationWord = data.getExtras().getFloat(
						"DisplayDurationWord");
				DisplayDurationBed = data.getExtras().getFloat(
						"DisplayDurationBed");
				PaukRepetitions = data.getExtras().getInt("PaukRepetitions");
				vok.ProbabilityFactor = data.getExtras().getFloat(
						"ProbabilityFactor");
				vok.setAbfrageZufaellig(data.getExtras().getBoolean("Random"));
				vok.setAskAll(data.getExtras().getBoolean("AskAll"));
				int Language = data.getExtras().getInt("Language",Vokabel.EnumSprachen.undefiniert.ordinal());
				for (int i = 0; i < Vokabel.EnumSprachen.values().length; i++)
				{
					if (Vokabel.EnumSprachen.values()[i].ordinal() == Language)
					{
						vok.setSprache(Vokabel.EnumSprachen.values()[i]);
						break;
					}
				}
				lib.sndEnabled = data.getExtras().getBoolean("Sound");
				Colors = getColorsFromIntent(data);
				colSounds = getSoundsFromIntent(data);

				libLearn.gStatus = "writing values to prefs";
				Editor editor = prefs.edit();
				editor.putInt("Schrittweite", vok.getSchrittweite());
				editor.putString("CharsetASCII", vok.CharsetASCII);
				editor.putInt("Abfragebereich", vok.getAbfragebereich());
				editor.putFloat("DisplayDurationWord", DisplayDurationWord);
				editor.putFloat("DisplayDurationBed", DisplayDurationBed);
				editor.putInt("PaukRepetitions", PaukRepetitions);
				editor.putFloat("ProbabilityFactor", vok.ProbabilityFactor);
				editor.putBoolean("Random", vok.getAbfrageZufaellig());
				editor.putBoolean("AskAll", vok.getAskAll());
				editor.putBoolean("Sound", lib.sndEnabled);

				for (ColorItems item : Colors.keySet()) {
					editor.putInt(item.name(), Colors.get(item).ColorValue);
				}

				for (Sounds item : colSounds.keySet()) {
					editor.putString(item.name(), colSounds.get(item).SoundPath);
				}

				editor.commit();
				libLearn.gStatus = "setTextColors";
				setTextColors();
				libLearn.gStatus = "getVokabel";
				getVokabel(false, false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lib.ShowException(_MainActivity.this, e);
		}
	}

	public void LoadVokabel(String fileSelected, int index, int[] Lernvokabeln,
			int Lernindex, boolean CardMode) {
		try {
			saveVok(false);
			try {
				vok.LoadFile(fileSelected, false, false, _blnUniCode);
			} catch (RuntimeException ex) {
				if (ex.getCause() != null) {
					if (ex.getCause().getMessage() != null
							&& ex.getCause().getMessage()
									.contains("IsSingleline")) {
						vok.LoadFile(fileSelected, true, false, _blnUniCode);
					} else {
						throw ex;
					}
				} else {
					throw ex;
				}
			}

			if (vok.getCardMode() || CardMode) {

				// _txtWord.setMaxLines(3);
				// _txtWord.setLines(2);
				_txtWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (40 * scale));

				// _txtKom.setMaxLines(3);
				// _txtKom.setLines(2);
				_txtKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (30 * scale));

				_txtMeaning1.setSingleLine(false);
				_txtMeaning1.setMaxLines(30);
				_txtMeaning1.setLines(16);
				_txtMeaning1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (20 * scale));
				// _txtMeaning1.setImeOptions(EditorInfo.IME_NULL);
				// _txtMeaning1.setImeActionLabel(null, KeyEvent.KEYCODE_ENTER);
				// _txtMeaning1.setImeActionLabel("Custom text",
				// KeyEvent.KEYCODE_ENTER);
				_txtMeaning2.setVisibility(View.GONE);
				_txtMeaning3.setVisibility(View.GONE);
			} else {

				// _txtWord.setMaxLines(3);
				// _txtWord.setLines(1);
				_txtWord.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (60 * scale));
				_txtWord.setHorizontallyScrolling(false);

				// _txtKom.setMaxLines(3);
				// _txtKom.setLines(2);
				_txtKom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (35 * scale));
				_txtKom.setHorizontallyScrolling(false);

				_txtMeaning1.setLines(1);
				_txtMeaning1.setSingleLine();
				_txtMeaning1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						(float) (40 * scale));
				_txtMeaning1.setMaxLines(3);
				_txtMeaning1.setHorizontallyScrolling(false);

				_txtMeaning2.setVisibility(View.VISIBLE);
				_txtMeaning2.setLines(1);
				_txtMeaning2.setSingleLine();
				_txtMeaning2.setMaxLines(3);
				_txtMeaning2.setHorizontallyScrolling(false);

				_txtMeaning3.setVisibility(View.VISIBLE);
				_txtMeaning3.setLines(1);
				_txtMeaning3.setSingleLine();
				_txtMeaning3.setMaxLines(3);
				_txtMeaning3.setHorizontallyScrolling(false);

			}

			// if (index >0 ) vok.setIndex(index);
			if (Lernvokabeln != null)
				vok.setLernvokabeln(Lernvokabeln);
			if (Lernindex > 0)
				vok.setLernIndex((short) Lernindex);
			if (vok.getGesamtzahl() > 1)
				setBtnsEnabled(true);
			getVokabel(false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			lib.ShowException(this, e);
		}
	}

	private HashMap<ColorItems, ColorSetting> getColorsFromPrefs() {
		HashMap<ColorItems, ColorSetting> res = new HashMap<ColorItems, ColorSetting>();
		for (int i = 0; i < ColorSetting.ColorItems.values().length; i++) {
			ColorItems ColorItem = ColorSetting.ColorItems.values()[i];
			String Name = getResources().getStringArray(R.array.spnColors)[i];
			int defValue = 0;
			switch (ColorItem) {
			case word:
				defValue = 0xff000000;
				break;
			case meaning:
				defValue = 0xff000000;
				break;
			case comment:
				defValue = 0xff000000;
				break;
			case background:
				defValue = 0xffffffff;
				break;
			case background_wrong:
				defValue = 0xffc0c0c0;
				break;
			case box_word:
				defValue = 0xffffffff;
				break;
			case box_meaning:
				defValue = 0xffffffff;
				break;
			default:
				defValue = 0xff000000;
				break;
			}
			int Color = prefs.getInt(ColorItem.name(), defValue);
			res.put(ColorItem, new ColorSetting(ColorItem, Name, Color));
		}
		return res;

	}

	private HashMap<ColorItems, ColorSetting> getColorsFromIntent(Intent intent) {
		HashMap<ColorItems, ColorSetting> res = new HashMap<ColorItems, ColorSetting>();
		for (int i = 0; i < ColorSetting.ColorItems.values().length; i++) {
			ColorItems ColorItem = ColorSetting.ColorItems.values()[i];
			String Name = getResources().getStringArray(R.array.spnColors)[i];
			int defValue = 0;
			switch (ColorItem) {
			case word:
				defValue = 0xff000000;
				break;
			case meaning:
				defValue = 0xff000000;
				break;
			case comment:
				defValue = 0xff000000;
				break;
			case background:
				defValue = 0xffffffff;
				break;
			case background_wrong:
				defValue = 0xffc0c0c0;
				break;
			case box_word:
				defValue = 0xffffffff;
				break;
			case box_meaning:
				defValue = 0xffffffff;
				break;
			default:
				defValue = 0xff000000;
				break;
			}
			int Color = intent.getIntExtra(ColorItem.name(), defValue);
			res.put(ColorItem, new ColorSetting(ColorItem, Name, Color));
		}
		return res;

	}

	private HashMap<Sounds, SoundSetting> getSoundsFromIntent(Intent intent) {
		HashMap<Sounds, SoundSetting> res = new HashMap<Sounds, SoundSetting>();
		if (lib.AssetSounds[0] == null)
			lib.initSounds();
		for (int i = 0; i < lib.Sounds.values().length; i++) {
			Sounds SoundItem = Sounds.values()[i];
			String Name = getResources().getStringArray(R.array.spnSounds)[i];
			String defValue = "";
			defValue = lib.AssetSounds[SoundItem.ordinal()];
			String SoundPath = intent.getStringExtra(SoundItem.name());
			if (libString.IsNullOrEmpty(SoundPath)) {
				SoundPath = defValue;
			}
			res.put(SoundItem, new SoundSetting(SoundItem, Name, SoundPath));
		}
		return res;

	}

	private HashMap<Sounds, SoundSetting> getSoundsFromPrefs() {
		HashMap<Sounds, SoundSetting> res = new HashMap<Sounds, SoundSetting>();
		if (lib.AssetSounds[0] == null)
			lib.initSounds();
		for (int i = 0; i < lib.Sounds.values().length; i++) {
			Sounds SoundItem = Sounds.values()[i];
			String Name = getResources().getStringArray(R.array.spnSounds)[i];
			String defValue = "";
			defValue = lib.AssetSounds[SoundItem.ordinal()];
			String SoundPath = prefs.getString(SoundItem.name(), defValue);
			res.put(SoundItem, new SoundSetting(SoundItem, Name, SoundPath));
		}
		return res;

	}

	public void getVokabel(boolean showBeds, boolean LoadNext) {
		try {
			EndEdit();
			setBtnsEnabled(true);
			if (showBeds) {
				_btnRight.setEnabled(true);
				_btnWrong.setEnabled(true);
			} else {
				_btnRight.setEnabled(false);
				_btnWrong.setEnabled(false);
			}
			if (LoadNext)
				vok.setLernIndex((short) (vok.getLernIndex() + 1));

			View v = findViewById(R.id.word);
			TextView t = (TextView) v;
			/*
			 * if (!vok.getCardMode()) { Rect bounds = new Rect(); Paint
			 * textPaint = t.getPaint();
			 * textPaint.getTextBounds(vok.getWort(),0,
			 * vok.getWort().length(),bounds); if (t.getWidth() <
			 * bounds.width()){ //int lines = t.getLineCount(); t.setLines((2));
			 * 
			 * if (((float)bounds.width() / (float)t.getWidth()) > 2) {
			 * t.setLines(3); } else { t.setLines((2)); }
			 * 
			 * } else { t.setLines(1); } }
			 */
			t.setText(getSpanned(vok.getWort()), TextView.BufferType.SPANNABLE);
			if (vok.getSprache() == EnumSprachen.Hebrew
					|| vok.getSprache() == EnumSprachen.Griechisch
					|| (vok.getFontWort().getName() == "Cardo")) {
				t.setTypeface(vok.TypefaceCardo);
				_txtedWord.setTypeface(vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
				_txtedWord.setTypeface(Typeface.DEFAULT);
			}

			v = findViewById(R.id.Comment);
			t = (TextView) v;
			t.setText(getSpanned(vok.getKommentar()),
					TextView.BufferType.SPANNABLE);
			if (vok.getSprache() == EnumSprachen.Hebrew
					|| vok.getSprache() == EnumSprachen.Griechisch
					|| (vok.getFontKom().getName() == "Cardo")) {
				t.setTypeface(vok.TypefaceCardo);
				_txtedKom.setTypeface(vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
				_txtedKom.setTypeface(Typeface.DEFAULT);
			}

			v = findViewById(R.id.txtMeaning1);
			t = (TextView) v;
			if (!libString.IsNullOrEmpty(vok.getBedeutung2()))
				t.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			t.setText((showBeds ? vok.getBedeutung1() : getComment(vok
					.getBedeutung1())));
			if (vok.getFontBed().getName() == "Cardo") {
				t.setTypeface(vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
			}
			t.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus && _firstFocus) {
						hideKeyboard();
						_firstFocus = false;
					}
				}
			});

			v = findViewById(R.id.txtMeaning2);
			t = (TextView) v;
			t.setText((showBeds ? vok.getBedeutung2() : getComment(vok
					.getBedeutung2())));
			if (vok.getFontBed().getName() == "Cardo") {
				t.setTypeface(vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
			}
			if (libString.IsNullOrEmpty(vok.getBedeutung2())
					|| vok.getCardMode()) {
				t.setVisibility(View.GONE);
				_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				t.setVisibility(View.VISIBLE);
				_txtMeaning1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			}

			v = findViewById(R.id.txtMeaning3);
			t = (TextView) v;
			t.setText((showBeds ? vok.getBedeutung3() : getComment(vok
					.getBedeutung3())));
			if (vok.getFontBed().getName() == "Cardo") {
				t.setTypeface(vok.TypefaceCardo);
			} else {
				t.setTypeface(Typeface.DEFAULT);
			}
			if (libString.IsNullOrEmpty(vok.getBedeutung3())
					|| vok.getCardMode()) {
				t.setVisibility(View.GONE);
				_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				t.setVisibility(View.VISIBLE);
				_txtMeaning2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				_txtMeaning3.setImeOptions(EditorInfo.IME_ACTION_DONE);
			}

			SetActionBarTitle();
			hideKeyboard();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			lib.ShowException(this, e);
		}

	}

	private String getComment(String vok) {
		int Start1 = vok.indexOf("[");
		if (Start1 > -1) {
			int Start2 = vok.indexOf("]", Start1 + 1);
			if (Start2 > Start1) {
				return vok.substring(Start1, Start2 + 1);
			}

		}
		return "";
	}

	private void SetActionBarTitle() throws Exception {
		if (vok.getGesamtzahl() > 0) {
			String title = "Learn " + (new File(vok.getFileName())).getName()
					+ " " + getString(R.string.number) + ": " + vok.getIndex()
					+ " " + getString(R.string.counter) + ": "
					+ vok.getZaehler();
			String Right = " " + vok.AnzRichtig;
			String Wrong = " " + vok.AnzFalsch;
			SpannableString spnTitle = new SpannableString(title);
			SpannableString spnRight = new SpannableString(Right);
			SpannableString spnWrong = new SpannableString(Wrong);
			spnRight.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					spnRight.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			spnWrong.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spnWrong.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

			getSupportActionBar().setTitle(
					TextUtils.concat(spnTitle, spnRight, spnWrong));

		} else {
			/*
			 * String title = "Learn " + "empty.vok" + " " +
			 * getString(R.string.number) + ": " + vok.getIndex() + " " +
			 * getString(R.string.counter) + ": " + vok.getZaehler(); String
			 * Right = " " + vok.AnzRichtig; String Wrong = " " + vok.AnzFalsch;
			 * SpannableString spnTitle = new SpannableString(title);
			 * SpannableString spnRight = new SpannableString(Right);
			 * SpannableString spnWrong = new SpannableString(Wrong);
			 * spnRight.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
			 * spnRight.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			 * spnWrong.setSpan(new ForegroundColorSpan(Color.RED), 0,
			 * spnWrong.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			 * 
			 * getSupportActionBar().setTitle( TextUtils.concat(spnTitle,
			 * spnRight, spnWrong));
			 */
		}
		resizeActionbar(0);
	}

	public Spanned getSpanned(String txt) throws IOException {
		if (txt.startsWith("{\\rtf1\\")) {
			// txt = Java2Html.convertToHtml(txt,
			// JavaSourceConversionOptions.getDefault());
			// return Html.fromHtml(txt);
			// return new SpannedString(stripRtf(txt));
		}
		return new SpannedString(txt);

	}

	public String stripRtf(String str) {
		String basicRtfPattern = "/\\{\\*?\\\\[^{}]+}|[{}]|\\\\[A-Za-z]+\\n?(?:-?\\d+)?[ ]?/g";
		String newLineSlashesPattern = "/\\\\\\n/g";

		String stripped = str.replaceAll(basicRtfPattern, "");
		String removeNewlineSlashes = stripped.replaceAll(
				newLineSlashesPattern, "\n");
		String removeWhitespace = removeNewlineSlashes.trim();

		return removeWhitespace;
	}
}