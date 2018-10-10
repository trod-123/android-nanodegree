package com.zn.expirytracker.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link DialogFragment} that prompts the user with a custom {@link android.widget.EditText} input
 */
public class TextInputDialogFragment extends DialogFragment {

    public static final String ARG_DIALOG_TITLE = Toolbox.createStaticKeyString(
            "text_input_dialog.dialog_title");
    private OnTextConfirmedListener callback;
    private OnDialogCancelListener cancelCallback;

    @BindView(R.id.et_dialog_name)
    TextInputEditText mEt;

    private View mView;
    private String mTitle;

    public static TextInputDialogFragment newInstance(String dialogTitle) {
        TextInputDialogFragment fragment = new TextInputDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnTextConfirmedListener {
        void onTextConfirmed(int position, String textInput);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (OnTextConfirmedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnTextConfirmedListener");
        }
        try {
            cancelCallback = (OnDialogCancelListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement " +
                    "OnDialogCancelListener");
        }

        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString(ARG_DIALOG_TITLE);
        }

        // For custom dialog layouts, necessary to inflate layout and bind to ButterKnife before
        // onCreateDialog()
        // https://stackoverflow.com/questions/37173221/bind-butterknife-to-dialog-fails
        mView = getLayoutInflater().inflate(R.layout.dialog_text_input, null);
        ButterKnife.bind(this, mView);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                .setView(mView)
                .setPositiveButton(R.string.action_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = mEt.getText().toString();
                                if (verifyTextInput(text)) {
                                    callback.onTextConfirmed(i, text);
                                } else {
                                    Toolbox.showToast(getContext(),
                                            getString(R.string.edit_error_required_name));
                                    reshowDialog();
                                }
                            }
                        })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onTextConfirmed(i, null);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancelCallback.onCancelled(this.getClass(), dialog);
    }

    /**
     * Non-empty text is required
     *
     * @param input
     * @return {@code false} if invalid
     */
    private boolean verifyTextInput(String input) {
        return !input.trim().isEmpty();
    }

    /**
     * Reshow the dialog if user entered text that did not satisfy conditions in
     * {@link TextInputDialogFragment#verifyTextInput(String)}
     */
    private void reshowDialog() {
        TextInputDialogFragment dialog = newInstance(mTitle);
        // needed so calling fragment remains the same
        dialog.setTargetFragment(getTargetFragment(), 0);

        dialog.show(getFragmentManager(), TextInputDialogFragment.class.getSimpleName());
    }
}
