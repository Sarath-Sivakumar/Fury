package app.personal.Utls;

import static uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt.STATE_FINISHED;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.ArrayList;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.fury.R;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class TutorialUtil {

    private final Context context;
    private final LifecycleOwner lOwner;
    private final AppUtilViewModel appVM;
    private final MutableLiveData<Integer> PhaseStatus = new MutableLiveData<>();
    private final MaterialTapTargetPrompt.Builder builder;

    public TutorialUtil(Activity activity, Context context, ViewModelStoreOwner owner, LifecycleOwner lOwner) {
        this.context = context;
        this.lOwner = lOwner;
        this.builder = new MaterialTapTargetPrompt.Builder(activity);
        appVM = new ViewModelProvider(owner).get(AppUtilViewModel.class);
    }

    public MutableLiveData<Integer> isPhaseStatus() {
        return PhaseStatus;
    }

    public void setPhaseStatus(int val){
        PhaseStatus.postValue(val);
    }


    private void Tutorial(View Target, String PrimaryText, String SecondaryText) {
        builder.setTarget(Target);
        builder.setPrimaryTextGravity(Gravity.CENTER);
        builder.setSecondaryTextGravity(Gravity.CENTER);
        builder.setTextGravity(Gravity.CENTER);
        builder.setPrimaryText(PrimaryText);
        builder.setSecondaryText(SecondaryText);
        builder.setPrimaryTextSize(64.0f);
        builder.setSecondaryTextSize(46.0f);
        builder.setPrimaryTextTypeface(Typeface.create(Typeface.SANS_SERIF, 800,true));
        builder.setSecondaryTextTypeface(Typeface.create(Typeface.SANS_SERIF,600,true));
        builder.setPrimaryTextColour(context.getResources().getColor(R.color.h1_dark));
        builder.setSecondaryTextColour(context.getResources().getColor(R.color.white));
        builder.setBackgroundColour(context.getResources().getColor(R.color.theme_green_dark));
        builder.setFocalColour(context.getResources().getColor(R.color.full_white));
        builder.setBackButtonDismissEnabled(false);
        builder.show();
    }

    public void TutorialPhase1(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt, state) -> {
                        if (state == STATE_FINISHED) {
                            Tutorial(Targets.get(1), PrimaryTexts.get(1), SecondaryTexts.get(1));
                            builder.setPromptStateChangeListener((prompt1, state1) -> {
                                if (state1 == STATE_FINISHED) {
                                    Log.e("Tutorial", "Phase 1 Complete.");
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    public void TutorialPhase2(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt1, state1) -> {
                        if (state1 == STATE_FINISHED) {
                            Log.e("Tutorial", "Phase 2 Complete.");
                            PhaseStatus.postValue(1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    public void TutorialPhase3(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt1, state1) -> {
                        if (state1 == STATE_FINISHED) {
                            Log.e("Tutorial", "Phase 3 Complete.");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    public void TutorialPhase4(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt1, state1) -> {
                        if (state1 == STATE_FINISHED) {
                            Log.e("Tutorial", "Phase 4 Complete.");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    public void TutorialPhase5(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt1, state1) -> {
                        if (state1 == STATE_FINISHED) {
                            Log.e("Tutorial", "Phase 5 Complete.");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    public void TutorialPhase6(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt1, state1) -> {
                        if (state1 == STATE_FINISHED) {
                            Log.e("Tutorial", "Phase 6 Complete.");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    public void TutorialPhase7(ArrayList<View> Targets, ArrayList<String> PrimaryTexts, ArrayList<String> SecondaryTexts) {
        builder.setAutoDismiss(false);
        appVM.getCheckerData().observe(lOwner, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Tutorial(Targets.get(0), PrimaryTexts.get(0), SecondaryTexts.get(0));
                    builder.setPromptStateChangeListener((prompt1, state1) -> {
                        if (state1 == STATE_FINISHED) {
                            Log.e("Tutorial", "Phase 7 Complete.");
                            appVM.getCheckerData().observe(lOwner, launchChecker1 -> {
                                if (launchChecker1.getTimesLaunched()==0){
                                    appVM.UpdateLaunchChecker(new LaunchChecker(launchChecker1.getId(),
                                            launchChecker.getTimesLaunched()+1));
                                    appVM.getCheckerData().removeObservers(lOwner);
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                appVM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }
}
