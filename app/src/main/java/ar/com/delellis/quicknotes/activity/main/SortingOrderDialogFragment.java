/*
 * Nextcloud Quicknotes Android client application.
 *
 * @copyright Copyright (c) 2020 Matias De lellis <mati86dl@gmail.com>
 *
 * @author Matias De lellis <mati86dl@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ar.com.delellis.quicknotes.activity.main;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import ar.com.delellis.quicknotes.R;

/**
 * Dialog to show and choose the sorting order for the file listing.
 */
public class SortingOrderDialogFragment extends DialogFragment {

    private final static String TAG = SortingOrderDialogFragment.class.getSimpleName();

    public static final String SORTING_ORDER_FRAGMENT = "SORTING_ORDER_FRAGMENT";
    private static final String KEY_SORT_ORDER = "SORT_ORDER";

    private View mView;
    private View[] mTaggedViews;
    private Button mCancel;

    private int mCurrentSortOrder;

    public static SortingOrderDialogFragment newInstance(int sortOrder) {
        SortingOrderDialogFragment dialogFragment = new SortingOrderDialogFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_SORT_ORDER, sortOrder);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep the state of the fragment on configuration changes
        setRetainInstance(true);

        mView = null;

        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments may not be null");
        }
        mCurrentSortOrder = arguments.getInt(KEY_SORT_ORDER, NoteAdapter.SORT_BY_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.sorting_order_fragment, container, false);

        setupDialogElements(mView);
        setupListeners();

        return mView;
    }

    /**
     * find all relevant UI elements and set their values.
     *
     * @param view the parent view
     */
    private void setupDialogElements(View view) {
        mCancel = view.findViewById(R.id.cancel);

        mTaggedViews = new View[6];
        mTaggedViews[0] = view.findViewById(R.id.sortByTitleAscending);
        mTaggedViews[0].setTag(NoteAdapter.SORT_BY_TITLE);
        mTaggedViews[1] = view.findViewById(R.id.sortByTitleAscendingText);
        mTaggedViews[1].setTag(NoteAdapter.SORT_BY_TITLE);
        mTaggedViews[2] = view.findViewById(R.id.sortByModificationDateDescending);
        mTaggedViews[2].setTag(NoteAdapter.SORT_BY_UPDATED);
        mTaggedViews[3] = view.findViewById(R.id.sortByModificationDateDescendingText);
        mTaggedViews[3].setTag(NoteAdapter.SORT_BY_UPDATED);
        mTaggedViews[4] = view.findViewById(R.id.sortByCreationDateDescending);
        mTaggedViews[4].setTag(NoteAdapter.SORT_BY_CREATED);
        mTaggedViews[5] = view.findViewById(R.id.sortByCreationDateDescendingText);
        mTaggedViews[5].setTag(NoteAdapter.SORT_BY_CREATED);

        setupActiveOrderSelection();
    }

    /**
     * tints the icon reflecting the actual sorting choice in the apps primary color.
     */
    private void setupActiveOrderSelection() {
        for (View view: mTaggedViews) {
            if (mCurrentSortOrder != (int) view.getTag()) {
                continue;
            }
            int tintColor =  view.getResources().getColor(R.color.fg_default_selection);
            if (view instanceof ImageButton) {
                Drawable normalDrawable = ((ImageButton) view).getDrawable();
                Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
                DrawableCompat.setTint(wrapDrawable, tintColor);
            }
            if (view instanceof TextView) {
                ((TextView)view).setTextColor(tintColor);
                ((TextView)view).setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
    }

    /**
     * setup all listeners.
     */
    private void setupListeners() {
        mCancel.setOnClickListener(view -> dismiss());

        OnSortOrderClickListener sortOrderClickListener = new OnSortOrderClickListener();

        for (View view : mTaggedViews) {
            view.setOnClickListener(sortOrderClickListener);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private class OnSortOrderClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismissAllowingStateLoss();
            ((SortingOrderDialogFragment.OnSortingOrderListener) getActivity())
                    .onSortingOrderChosen((int) v.getTag());
        }
    }

    public interface OnSortingOrderListener {
        void onSortingOrderChosen(int sortSelection);
    }
}
