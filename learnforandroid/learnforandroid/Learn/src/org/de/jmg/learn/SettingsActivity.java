/*
 * Copyright (c) 2015 GPL by J.M.Goebel. Distributed under the GNU GPL v3.
 * 
 * 08.06.2015
 * 
 * This file is part of learnforandroid.
 *
 * learnforandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  learnforandroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.de.jmg.learn;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import br.com.thinkti.android.filechooser.FileChooser;

import org.de.jmg.lib.AbstractScaledArrayAdapter;
import org.de.jmg.lib.ColorsArrayAdapter;
import org.de.jmg.lib.ScaledArrayAdapter;
import org.de.jmg.lib.SoundSetting;
import org.de.jmg.lib.SoundsArrayAdapter;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.lib.libString;
import org.de.jmg.lib.ColorSetting;
import yuku.ambilwarna.*;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

public class SettingsActivity extends AppCompatActivity {
	private static final int FILE_CHOOSER = 0x42FA;
	public Spinner spnAbfragebereich;
	public Spinner spnASCII;
	public Spinner spnStep;
	public Spinner spnDisplayDurationWord;
	public Spinner spnDisplayDurationBed;
	public Spinner spnPaukRepetitions;
	public Spinner spnProbabilityFactor;
	public Spinner spnLanguages;
	public org.de.jmg.lib.NoClickSpinner spnColors;
	public org.de.jmg.lib.NoClickSpinner spnSounds;
	public Button btnColors;
	public CheckBox chkRandom;
	public CheckBox chkAskAll;
	public CheckBox chkSound;
	public CheckBox chkDocumentProvider;
	public CheckBox chkDontShowPersistableURIMessage;
	CheckBox chkAlwaysStartExternalProgram;
	public ColorsArrayAdapter Colors;
	public SoundsArrayAdapter Sounds;
	public SharedPreferences prefs;
	private View mainView;
	private Intent intent = new Intent();
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
			//lib.ShowToast(this, "Settings Start");
			if (lib.NookSimpleTouch())
			{
				setContentView(R.layout.activity_settings_nook);
			}
			else
			{
			setContentView(R.layout.activity_settings);
			}
			RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.layoutSettings); // id fetch from xml
			ShapeDrawable rectShapeDrawable = new ShapeDrawable(); // pre defined class
			int pxPadding = lib.dpToPx(10);
			rectShapeDrawable.setPadding(pxPadding, pxPadding, pxPadding, pxPadding * ((lib.NookSimpleTouch()) ? 2 : 1));
			Paint paint = rectShapeDrawable.getPaint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(5); // you can change the value of 5
			lib.setBg(layout, rectShapeDrawable);
			
			mainView = findViewById(Window.ID_ANDROID_CONTENT);
			Thread.setDefaultUncaughtExceptionHandler(ErrorHandler);
			prefs = this.getPreferences(Context.MODE_PRIVATE);
			
			Colors = new ColorsArrayAdapter(this,
					android.R.layout.simple_spinner_item);
			Colors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			Sounds = new SoundsArrayAdapter(this,
					android.R.layout.simple_spinner_item);
			Sounds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					
			TextView txtSettings = (TextView) findViewById(R.id.txtSettings);
			SpannableString Settings = new SpannableString(txtSettings.getText());
			Settings.setSpan(new UnderlineSpan(), 0, Settings.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			txtSettings.setText(Settings);
			initSpinners();
			initCheckBoxes();
			initButtons();
			
			// resize();
			//lib.ShowToast(this, "Settings addlayoutlistener");
			if (!(lib.NookSimpleTouch()))
			{
				mainView.getViewTreeObserver().addOnGlobalLayoutListener(
						new ViewTreeObserver.OnGlobalLayoutListener() {

							@Override
							public void onGlobalLayout() {
								// Ensure you call it only once :
								lib.removeLayoutListener(mainView.getViewTreeObserver(), this);
								
								// Here you can get the size :)
								resize(0);
								//lib.ShowToast(SettingsActivity.this, "Resize End");
							}
						});

			}
			else
			{
				//resize(1.8f);
			}
			
		}
		catch (Exception ex)
		{
			lib.ShowException(this, ex);
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		handler.removeCallbacks(rSetBackPressedFalse);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		try {
			getMenuInflater().inflate(R.menu.settings, menu);
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
		if (id == R.id.mnuResize && !lib.NookSimpleTouch()) resize(0);
		return super.onOptionsItemSelected(item);
	}
	
	private void initCheckBoxes() {
		chkRandom = (CheckBox) findViewById(R.id.chkRandom);
		chkAskAll = (CheckBox) findViewById(R.id.chkAskAll);
		chkSound = (CheckBox) findViewById(R.id.chkSound);
		chkDocumentProvider = (CheckBox) findViewById(R.id.chkDocumentProvider);
		chkDontShowPersistableURIMessage = (CheckBox) findViewById(R.id.chkDontShowPersistableURIMessage);
		chkAlwaysStartExternalProgram = (CheckBox) findViewById(R.id.chkAlwaysStartExternalProgram);
		boolean checked = getIntent().getBooleanExtra("Random", false);
		chkRandom.setChecked(checked);
		intent.putExtra("Random", checked);

		chkRandom.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				intent.putExtra("Random", isChecked);
			}

		});

		checked = getIntent().getBooleanExtra("AskAll", false);
		chkAskAll.setChecked(checked);
		intent.putExtra("AskAll", checked);

		chkAskAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				intent.putExtra("AskAll", isChecked);
			}

		});

		checked = getIntent().getBooleanExtra("Sound", true);
		chkSound.setChecked(checked);
		intent.putExtra("Sound", checked);

		chkSound.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				intent.putExtra("Sound", isChecked);
			}

		});
		if (Build.VERSION.SDK_INT< 19)
		{
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) chkRandom
					.getLayoutParams();
			params.bottomMargin = (int) lib.dpToPx(10);
			chkRandom.setLayoutParams(params);
			
			chkDocumentProvider.setVisibility(View.GONE);
			chkDontShowPersistableURIMessage.setVisibility(View.GONE);
		}
		else
		{
			final String keyProvider = "ShowAlwaysDocumentProvider";
			int ShowAlwaysDocumentProvider = getIntent().getIntExtra(keyProvider, 999);
			intent.putExtra(keyProvider, ShowAlwaysDocumentProvider);
			final String keyURIMessage = "DontShowPersistableURIMessage";
			int DontShowPersistableURIMessage = getIntent().getIntExtra(keyURIMessage, 999);
			intent.putExtra(keyURIMessage, DontShowPersistableURIMessage);
			
			if (ShowAlwaysDocumentProvider==-1) 
			{
				chkDocumentProvider.setChecked(true);
			}
			else if (ShowAlwaysDocumentProvider==0)
			{
				chkDocumentProvider.setChecked(false);
			}
			else
			{
				chkDocumentProvider.setEnabled(false);
			}
			chkDocumentProvider.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					int ShowAlwaysDocumentProvider = isChecked?-1:0;
					if(!isChecked)
					{
						String msg=SettingsActivity.this.getString(R.string.msgResetSetting);
						if(lib.ShowMessageYesNo(SettingsActivity.this, msg, ""))
						{
							ShowAlwaysDocumentProvider=999;	
						}
					}
					intent.putExtra(keyProvider, ShowAlwaysDocumentProvider);
					
				}
			});
			
			
			
			if (DontShowPersistableURIMessage==-1) 
			{
				chkDontShowPersistableURIMessage.setChecked(true);
			}
			else if (DontShowPersistableURIMessage==0)
			{
				chkDontShowPersistableURIMessage.setChecked(false);
			}
			else
			{
				chkDontShowPersistableURIMessage.setEnabled(false);
			}
			
			chkDontShowPersistableURIMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					int DontShowPersistableURIMessage = isChecked?-1:0;
					intent.putExtra(keyURIMessage, DontShowPersistableURIMessage);
				}
			});
		}
		
		final String key = "AlwaysStartExternalProgram";
		int AlwaysStartInternalProgram = getIntent().getIntExtra(key, 999);
		intent.putExtra(key, AlwaysStartInternalProgram);
		
		
		if (AlwaysStartInternalProgram==-1) 
		{
			chkAlwaysStartExternalProgram.setChecked(true);
		}
		else if (AlwaysStartInternalProgram==0)
		{
			chkAlwaysStartExternalProgram.setChecked(false);
		}
		else
		{
			chkAlwaysStartExternalProgram.setEnabled(false);
		}
		chkAlwaysStartExternalProgram.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				int AlwaysStartExternalProgram = isChecked?-1:0;
				if(!isChecked)
				{
					String msg=SettingsActivity.this.getString(R.string.msgResetSetting);
					if(lib.ShowMessageYesNo(SettingsActivity.this, msg, ""))
					{
						AlwaysStartExternalProgram=999;	
					}
				}
				intent.putExtra(key, AlwaysStartExternalProgram);
				
			}
		});
		
		

	}

	private void initSpinners() {
		libLearn.gStatus = "initSpinners";
		try {
			spnAbfragebereich = (Spinner) findViewById(R.id.spnAbfragebereich);
			spnASCII = (Spinner) findViewById(R.id.spnASCII);
			spnStep = (Spinner) findViewById(R.id.spnStep);
			spnDisplayDurationWord = (Spinner) findViewById(R.id.spnAnzeigedauerWord);
			spnDisplayDurationBed = (Spinner) findViewById(R.id.spnAnzeigedauerBed);
			spnPaukRepetitions = (Spinner) findViewById(R.id.spnRepetitions);
			spnProbabilityFactor = (Spinner) findViewById(R.id.spnProbabilityFactor);
			spnLanguages = (Spinner) findViewById(R.id.spnLanguages);
			spnColors = (org.de.jmg.lib.NoClickSpinner) findViewById(R.id.spnColors);
			spnSounds = (org.de.jmg.lib.NoClickSpinner) findViewById(R.id.spnSounds);

			spnASCII.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnStep.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnDisplayDurationBed.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnDisplayDurationWord.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnAbfragebereich.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnPaukRepetitions.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnProbabilityFactor.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			spnLanguages.getBackground().setColorFilter(Color.BLACK,
					PorterDuff.Mode.SRC_ATOP);
			//spnColors.getBackground().setColorFilter(Color.BLACK,	PorterDuff.Mode.SRC_ATOP);
			//spnSounds.getBackground().setColorFilter(Color.BLACK,	PorterDuff.Mode.SRC_ATOP);

			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			ScaledArrayAdapter<CharSequence> adapter = ScaledArrayAdapter
					.createFromResource(this, R.array.spnAbfragebereichEntries,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch()) adapter.Scale = 1.8f;
			// Apply the adapter to the spinner
			spnAbfragebereich.setAdapter(adapter);
			spnAbfragebereich.setSelection(getIntent().getShortExtra(
					"Abfragebereich", (short) -1) + 1);

			spnAbfragebereich
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							intent.putExtra("Abfragebereich",
									(short) (position - 1));

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							setResult(Activity.RESULT_CANCELED, null);
						}
					});
			ScaledArrayAdapter<CharSequence> adapterStep = ScaledArrayAdapter
					.createFromResource(this, R.array.spnStepEntries,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterStep
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			if (lib.NookSimpleTouch()) adapterStep.Scale = 1.8f;
			spnStep.setAdapter(adapterStep);
			spnStep.setSelection(adapterStep.getPosition(""
					+ getIntent().getShortExtra("Step", (short) 5)));
			spnStep.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					intent.putExtra("Step", (short) (Integer
							.parseInt((String) parent
									.getItemAtPosition(position))));

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					setResult(Activity.RESULT_CANCELED, null);
				}
			});

			ScaledArrayAdapter<String> adapterASCII = new ScaledArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item);
			// adapterASCII.addAll(Charset.availableCharsets().values());

			for (Charset c : Charset.availableCharsets().values()) {
				adapterASCII.add(c.name());
			}
			// Specify the layout to use when the list of choices appears
			adapterASCII
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			if (lib.NookSimpleTouch()) adapterASCII.Scale = 1.8f;
			spnASCII.setAdapter(adapterASCII);
			String CharsetASCII = getIntent().getStringExtra("CharsetASCII");
			if (!libString.IsNullOrEmpty(CharsetASCII)) {
				int i = 0;
				for (Charset c : Charset.availableCharsets().values()) {
					if (c.name().equalsIgnoreCase(CharsetASCII)) {
						break;
					}
					i++;
				}
				if (i < adapterASCII.getCount()) {
					spnASCII.setSelection(i);
				}

			}
			spnASCII.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					intent.putExtra("CharsetASCII",
							((String) (parent.getSelectedItem())));

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					setResult(Activity.RESULT_CANCELED, null);
				}
			});

			ScaledArrayAdapter<CharSequence> adapterDDWord = ScaledArrayAdapter
					.createFromResource(this, R.array.spnDurations,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterDDWord
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch()) adapterDDWord.Scale = 1.8f;
			// Apply the adapter to the spinner
			spnDisplayDurationWord.setAdapter(adapterDDWord);
			String strDD = ""
					+ getIntent().getFloatExtra("DisplayDurationWord", 1.5f);
			strDD = strDD.replace(".0", "");
			int Pos = adapterDDWord.getPosition(strDD);
			spnDisplayDurationWord.setSelection(Pos);
			spnDisplayDurationWord
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							intent.putExtra("DisplayDurationWord", (Float
									.parseFloat((String) parent
											.getItemAtPosition(position))));

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							setResult(Activity.RESULT_CANCELED, null);
						}
					});

			ScaledArrayAdapter<CharSequence> adapterDDBed = ScaledArrayAdapter
					.createFromResource(this, R.array.spnDurations,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterDDBed
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch()) adapterDDBed.Scale = 1.8f;
			// Apply the adapter to the spinner
			spnDisplayDurationBed.setAdapter(adapterDDBed);
			strDD = "" + getIntent().getFloatExtra("DisplayDurationBed", 2.5f);
			strDD = strDD.replace(".0", "");
			Pos = adapterDDBed.getPosition(strDD);
			spnDisplayDurationBed.setSelection(Pos);
			spnDisplayDurationBed
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							intent.putExtra("DisplayDurationBed", (Float
									.parseFloat((String) parent
											.getItemAtPosition(position))));

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							setResult(Activity.RESULT_CANCELED, null);
						}
					});

			ScaledArrayAdapter<CharSequence> adapterPaukRepetitions = ScaledArrayAdapter
					.createFromResource(this, R.array.spnRepetitions,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterPaukRepetitions
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch()) adapterPaukRepetitions.Scale = 1.8f;
			spnPaukRepetitions.setAdapter(adapterPaukRepetitions);
			Pos = getIntent().getIntExtra("PaukRepetitions", 3) - 1;
			spnPaukRepetitions.setSelection(Pos);
			spnPaukRepetitions
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							intent.putExtra("PaukRepetitions", (Integer
									.parseInt((String) parent
											.getItemAtPosition(position))));

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							setResult(Activity.RESULT_CANCELED, null);
						}
					});

			ScaledArrayAdapter<CharSequence> adapterProbabilityFactor = ScaledArrayAdapter
					.createFromResource(this, R.array.spnProbabilityFactors,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterProbabilityFactor
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch()) adapterProbabilityFactor.Scale = 1.8f;
			spnProbabilityFactor.setAdapter(adapterProbabilityFactor);
			float ProbabilityFactor = getIntent().getFloatExtra(
					"ProbabilityFactor", -1f);
			if (ProbabilityFactor == -1) {
				strDD = "auto";
			} else {
				strDD = "" + ProbabilityFactor;
				strDD = strDD.replace(".0", "");
			}

			ArrayAdapter<CharSequence> a1 = adapterProbabilityFactor;
			if (a1 != null) {
				try {
					libLearn.gStatus = "get Spinneradapter ProbabilityFactor";
					Pos = (a1.getPosition(strDD));
					spnProbabilityFactor.setSelection(Pos);
				} catch (Exception ex) {
					lib.ShowException(this, ex);
				}

			}

			spnProbabilityFactor
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							String strDD = (String) parent
									.getItemAtPosition(position);
							if (strDD.equalsIgnoreCase("auto"))
								strDD = "-1";
							intent.putExtra("ProbabilityFactor",
									(Float.parseFloat(strDD)));

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							setResult(Activity.RESULT_CANCELED, null);
						}
					});
			ScaledArrayAdapter<CharSequence> adapterLanguages = ScaledArrayAdapter
					.createFromResource(this, R.array.spnLanguages,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterLanguages
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (lib.NookSimpleTouch()) adapterLanguages.Scale = 1.8f;
			spnLanguages.setAdapter(adapterLanguages);
			int Language = getIntent().getIntExtra(
					"Language", org.de.jmg.learn.vok.Vokabel.EnumSprachen.undefiniert.ordinal());
			spnLanguages.setSelection(Language);

			spnLanguages
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							intent.putExtra("Language", position);

						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							setResult(Activity.RESULT_CANCELED, null);
						}
					});

			if (lib.NookSimpleTouch()) Colors.Scale = 1.8f;
			spnColors.setAdapter(Colors);
			spnColors
					.setOnLongClickListener(new android.widget.AdapterView.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							spnColors.blnDontCallOnClick = true;
							ShowColorDialog();
							return false;
						}
					});
			spnColors
					.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							spnColors.blnDontCallOnClick = true;
							ShowColorDialog();
							return false;
						}
					});
			
			if (lib.NookSimpleTouch()) Sounds.Scale = 1.8f;
			spnSounds.setAdapter(Sounds);
			spnSounds
					.setOnLongClickListener(new android.widget.AdapterView.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							spnSounds.blnDontCallOnClick = true;
							ShowSoundsDialog();
							return false;
						}
					});
			spnSounds
					.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							spnSounds.blnDontCallOnClick = true;
							ShowSoundsDialog();
							return false;
						}
					});

			spnSounds.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					SoundSetting item = (SoundSetting) parent
							.getItemAtPosition(position);
					File F = new File(item.SoundPath);
					try {
						if (F.exists())
							lib.playSound(F);
						else
							lib.playSound(getAssets(), item.SoundPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					setResult(Activity.RESULT_CANCELED, null);
				}
			});

		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}
	}

	private void initButtons() {
		Button b = (Button) findViewById(R.id.btnOK);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveResultsAndFinish();
			}
		});
		b = (Button) findViewById(R.id.btnCancel);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_CANCELED, intent);
				finish();
			}
		});

	}
	
	private void saveResultsAndFinish()
	{
		setResult(Activity.RESULT_OK, intent);
		for (int i = 0; i < Colors.getCount(); i++) {
			intent.putExtra(Colors.getItem(i).ColorItem.name(),
					Colors.getItem(i).ColorValue);
		}

		for (int i = 0; i < Sounds.getCount(); i++) {
			intent.putExtra(Sounds.getItem(i).Sound.name(),
					Sounds.getItem(i).SoundPath);
		}
		finish();
	}

	public float scale = 1;

	private void resize(float scale) {
		
		Resources resources = this.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		@SuppressWarnings("unused")
		int Density = metrics.densityDpi;
		try
		{
			int width = mainView.getWidth();
			if (width>500)
			{
				width -= lib.dpToPx(40);
			}
			else
			{
				width -= lib.dpToPx(40);
			}
			if (scale == 0)
			{
				mainView.setVisibility(View.INVISIBLE);
				libLearn.gStatus="Calculating Scale";
					
				float scale1 = width
						/ (float) ((findViewById(R.id.txtCharsetASCII)).getWidth()
								+ spnASCII.getWidth() + width / 50);
				float scale2 = width
						/ (float) ((findViewById(R.id.txtSounds)).getWidth()
								+ spnSounds.getWidth() + width / 50);
				float scale3 = width / (float)((findViewById(R.id.txtCharsetASCII)).getWidth()
						+ spnASCII.getWidth() + width / 50);
				scale = (scale1 < scale2) ? scale1 : scale2;
				scale = (scale3 < scale) ? scale3 : scale;
			}
			
			ViewGroup Settings = (ViewGroup) findViewById(R.id.layoutSettings);
			libLearn.gStatus = "Enumerating ChildViews";
			int ChildCount = Settings.getChildCount();
			for (int i = 0; i < ChildCount; i++) {
				if (i>100)break;
				libLearn.gStatus="getting view "+i;
				View V = Settings.getChildAt(i);
				
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) V
						.getLayoutParams();

				params.topMargin = (int) (params.topMargin * scale);
				params.bottomMargin = (int) (params.bottomMargin * scale);
				
				if (params.height>0) params.height = (int) (params.height * scale);
				if (V instanceof CheckBox)
				{
					if (params.width>0) params.width = ((width - lib.dpToPx(10))/3);
				}
				else
				{
					if (params.width>0)params.width = (int) (params.width * scale);
				}
				/*
				if (V == spnSounds) {
					int soundsHeight = spnSounds.getHeight();
					float margin = (float) ((soundsHeight * scale) / 5.25);
					params.topMargin = params.topMargin + (int) margin;
				}
				*/
				libLearn.gStatus="Setting Layoutparams";
				V.setLayoutParams(params);
				// }
				if (V instanceof TextView && !(V instanceof CheckBox)) {
					libLearn.gStatus="TextView set size";
					TextView t = (TextView) V;
					t.setTextSize(TypedValue.COMPLEX_UNIT_PX, t.getTextSize()
							* scale);
				} else if (V instanceof Spinner) {
					Spinner spn = (Spinner) V;
					SpinnerAdapter A = spn.getAdapter();
					if (A instanceof AbstractScaledArrayAdapter<?>) {
						libLearn.gStatus="Scaling Adapter";
						AbstractScaledArrayAdapter<?> AA = (AbstractScaledArrayAdapter<?>) A;
						AA.Scale = AA.Scale * scale;
						if (spn.getSelectedItemPosition()>-1)
						{
							AA.notifyDataSetChanged();
						}
						

					}
				} else if (V instanceof CheckBox) {
					libLearn.gStatus="CheckBox";
					CheckBox c = (CheckBox) V;
					//c.setScaleX(scale);
					//c.setScaleY(scale);
					// c.setle
					c.setTextSize(TypedValue.COMPLEX_UNIT_PX, c.getTextSize()
							* scale);
					
					/*
					int p1 = c.getPaddingTop();
					int p2 = c.getPaddingBottom();
					int p3 = c.getPaddingLeft();
					int p4 = c.getPaddingRight();
					c.setPadding((int) (p3/scale), p1, p4, p2);
					*/
					//LevelListDrawable D = (LevelListDrawable) c.getBackground();
					/*
					Drawable d = c.getBackground();
					Log.d("bounds", d.getBounds().toString());
					*/
					//d.setTargetDensity((int) (Density * scale));
					//d.setBounds(0, 0, c.getHeight(), c.getHeight());
					//lib.setBgCheckBox(c,d);
					/*
					ViewGroup check = (ViewGroup) V;
					for (int ii = 0; ii<check.getChildCount(); ii++)
					{
						View cv = check.getChildAt(ii);
						String cls = cv.getClass().getName();
						Log.d("Classs", cls);
					}
					*/
					// Drawable d = lib.getDefaultCheckBoxDrawable(this);
					// d = new ScaleDrawable(d, 0, c.getHeight()*scale,
					// c.getHeight()*scale).getDrawable();
					// float scaleC = (float)c.getHeight()/d.getBounds().height();
					// d.setBounds(0, 0,(int) (c.getHeight()*scale),(int)
					// (c.getHeight()*scale));
					// LayerDrawable L = new LayerDrawable(new Drawable[]{d});
					// d = lib.scaleImage(this, d, scaleC);
					// /c.setButtonDrawable(d);
				}
			}
			libLearn.gStatus="Buttons";
			Button b = (Button) findViewById(R.id.btnOK);
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) b
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			if (params.height>0)params.height = (int) (params.height * scale);
			if (params.width>0)params.width = (int) (params.width * scale);
			b.setLayoutParams(params);
			b.setTextSize(TypedValue.COMPLEX_UNIT_PX, b.getTextSize() * scale);

			b = (Button) findViewById(R.id.btnCancel);
			params = (android.widget.RelativeLayout.LayoutParams) b
					.getLayoutParams();
			params.topMargin = (int) (params.topMargin * scale);
			if (params.height>0)params.height = (int) (params.height * scale);
			if (params.width>0)params.width = (int) (params.width * scale);
			b.setLayoutParams(params);
			b.setTextSize(TypedValue.COMPLEX_UNIT_PX, b.getTextSize() * scale);
		}
		catch (Exception ex)
		{
			lib.ShowException(this, ex);
		}
		finally
		{
			mainView.setVisibility(View.VISIBLE);
		}
		
		
	}

	private void ShowColorDialog() {
		spnColors.blnDontCallOnClick = true;
		ColorSetting item = SettingsActivity.this.Colors.getItem(spnColors
				.getSelectedItemPosition());
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, item.ColorValue,
				new OnAmbilWarnaListener() {

					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						// TODO Auto-generated method stub
						ColorSetting item = SettingsActivity.this.Colors
								.getItem(spnColors.getSelectedItemPosition());
						item.ColorValue = color;
						Editor editor = prefs.edit();
						editor.putInt(item.ColorItem.name(), item.ColorValue);
						intent.putExtra(item.ColorItem.name(), item.ColorValue);
						;
						editor.commit();
						Colors.notifyDataSetChanged();
						spnColors.blnDontCallOnClick = false;
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// TODO Auto-generated method stub
						spnColors.blnDontCallOnClick = false;
					}
				});
		dialog.show();

	}

	private void ShowSoundsDialog() {
		spnSounds.blnDontCallOnClick = true;
		SoundSetting item = SettingsActivity.this.Sounds.getItem(spnSounds
				.getSelectedItemPosition());
		File F = new File(item.SoundPath);
		String dir = Environment.getExternalStorageDirectory().getPath();
		if (F.exists())
			dir = F.getParent();
		Intent intent = new Intent(this, FileChooser.class);
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".wav");
		extensions.add(".mp3");
		extensions.add(".ogg");
		extensions.add(".flv");

		intent.putStringArrayListExtra("filterFileExtension", extensions);
		intent.putExtra("DefaultDir", dir);

		this.startActivityForResult(intent, FILE_CHOOSER);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == FILE_CHOOSER
					&& (resultCode == Activity.RESULT_OK)) {
				String fileSelected = data.getStringExtra("fileSelected");
				SoundSetting item = SettingsActivity.this.Sounds
						.getItem(spnSounds.getSelectedItemPosition());
				item.SoundPath = fileSelected;
				File F = new File(item.SoundPath);
				try {
					if (F.exists())
						lib.playSound(F);
					else
						lib.playSound(getAssets(), item.SoundPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Editor editor = prefs.edit();
				editor.putString(item.Sound.name(), item.SoundPath);
				intent.putExtra(item.Sound.name(), item.SoundName);
				;
				editor.commit();
				Sounds.notifyDataSetChanged();
				spnSounds.blnDontCallOnClick = false;
			}
		} catch (Exception ex) {
			lib.ShowException(this, ex);
		}
	}
	
	private int _backPressed = 0;
	
	private Runnable rSetBackPressedFalse = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			_backPressed = 0;
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_HOME) 
		{
			try 
			{
				//saveVok(false);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				lib.ShowException(this, e);
				return true;
			}
		}
		else if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			try 
			{
				if (_backPressed > 0) 
				{
					handler.removeCallbacks(rSetBackPressedFalse);
				} else 
				{
					_backPressed += 1;
					handler.postDelayed(rSetBackPressedFalse, 10000);
					AlertDialog.Builder A = new AlertDialog.Builder(this);
					 A.setPositiveButton(getString(R.string.yes), new
					 AlertDialog.OnClickListener() 
					 {				  
						  @Override public void onClick(DialogInterface dialog, int which) 
						  { 
							  saveResultsAndFinish(); 
						  }
					 });
					 A.setNegativeButton(getString(R.string.no), new
					 AlertDialog.OnClickListener() 
					  {
						  @Override 
						  public void onClick(DialogInterface dialog, int which) 
						  { 
							setResult(Activity.RESULT_CANCELED, intent);
							finish();
						  }
					  }); 
					  A.setMessage(getString(R.string.msgSaveSettings));
					  A.setTitle(getString(R.string.question)); 
					  A.show();
					  return true;
				}
				

			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				Log.e("onBackPressed", e.getMessage(), e);
				lib.ShowException(this, e);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);

	};

	public UncaughtExceptionHandler ErrorHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// TODO Auto-generated method stub
			lib.ShowException(SettingsActivity.this, ex);
		}
	};

}
