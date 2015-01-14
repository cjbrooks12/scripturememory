package com.caseybrooks.scripturememory.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.defaults.DefaultMetaData;
import com.caseybrooks.scripturememory.R;
import com.caseybrooks.scripturememory.data.MetaSettings;
import com.caseybrooks.scripturememory.data.Util;
import com.caseybrooks.scripturememory.data.VerseDB;
import com.caseybrooks.scripturememory.misc.FlowLayout;
import com.caseybrooks.scripturememory.notifications.MainNotification;

import java.util.ArrayList;

public class EditVerseFragment extends Fragment {
//Data Members
//------------------------------------------------------------------------------
	Context context;
	View view;
    Passage passage;
    int listType;
    int listId;

	EditText editRef, editVer;

    TagAdapter tagAdapter;

    FlowLayout tagChipsLayout;
    TextView seekbarText;
    SeekBar seekbar;

    public static Fragment newInstance(int verseId, int listType, int listId) {
        Fragment fragment = new EditVerseFragment();
        Bundle extras = new Bundle();
        extras.putInt("KEY_ID", verseId);
        extras.putInt("KEY_LIST_TYPE", listType);
        extras.putInt("KEY_LIST_ID", listId);
        fragment.setArguments(extras);
        return fragment;
    }

//Lifecycle and Initialization
//------------------------------------------------------------------------------
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_edit_verse, container, false);
        context = getActivity();
        initialize();
      
        return view;
	}

    @Override
    public void onResume() {
        super.onResume();

        ActionBar ab = ((ActionBarActivity) context).getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.color_toolbar, typedValue, true);
        int color = typedValue.data;
        ColorDrawable colorDrawable = new ColorDrawable(color);
        ab.setBackgroundDrawable(colorDrawable);
        ab.setTitle("Edit");
    }

    private void initialize() {
		long id = getArguments().getInt("KEY_ID", 1);
        listType = MetaSettings.getActiveList(context).first;
        listId = MetaSettings.getActiveList(context).first;

        VerseDB db = new VerseDB(context).open();
		passage = db.getVerse(id);

        if(passage != null) {
            editRef = (EditText) view.findViewById(R.id.updateReference);
            editRef.setText(passage.getReference().toString());
            editVer = (EditText) view.findViewById(R.id.updateVerse);
            editVer.setText(passage.getText());

            seekbarText = (TextView) view.findViewById(R.id.seekbar_text);
            switch (passage.getMetadata().getInt(DefaultMetaData.STATE)) {
                case VerseDB.CURRENT_NONE:
                    seekbarText.setText("Current - None");
                    break;
                case VerseDB.CURRENT_SOME:
                    seekbarText.setText("Current - Some");
                    break;
                case VerseDB.CURRENT_MOST:
                    seekbarText.setText("Current - Most");
                    break;
                case VerseDB.CURRENT_ALL:
                    seekbarText.setText("Current - All");
                    break;
                case VerseDB.MEMORIZED:
                    seekbarText.setText("Memorized");
                    break;
            }

            seekbar = (SeekBar) view.findViewById(R.id.stateSeekBar);
            seekbar.setProgress(passage.getMetadata().getInt(DefaultMetaData.STATE) - 1);

            int color = db.getStateColor(seekbar.getProgress() + 1);

            Drawable line = seekbar.getProgressDrawable();
            line.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            Drawable thumb = getResources().getDrawable(R.drawable.seekbar_thumb);
            thumb.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            seekbar.setThumb(thumb);

            seekbar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    VerseDB db = new VerseDB(context).open();
                    int color = db.getStateColor(progressValue + 1);
                    passage.getMetadata().putInt(DefaultMetaData.STATE, progressValue + 1);
                    db.updateVerse(passage);
                    db.close();

                    Drawable line = seekBar.getProgressDrawable();
                    line.setColorFilter(color, PorterDuff.Mode.SRC_IN);

                    Drawable thumb = getResources().getDrawable(R.drawable.seekbar_thumb);
                    thumb.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    seekbar.setThumb(thumb);

                    switch (passage.getMetadata().getInt(DefaultMetaData.STATE)) {
                        case VerseDB.CURRENT_NONE:
                            seekbarText.setText("Current - None");
                            break;
                        case VerseDB.CURRENT_SOME:
                            seekbarText.setText("Current - Some");
                            break;
                        case VerseDB.CURRENT_MOST:
                            seekbarText.setText("Current - Most");
                            break;
                        case VerseDB.CURRENT_ALL:
                            seekbarText.setText("Current - All");
                            break;
                        case VerseDB.MEMORIZED:
                            seekbarText.setText("Memorized");
                            break;
                    }

                    //if this verse is the current notification verse and the active list is its state, then
                    //change the active list to be whatever state this verse becomes
                    if(MetaSettings.getVerseId(context) == passage.getMetadata().getInt(DefaultMetaData.ID) &&
                            listType == VerseListFragment.STATE) {
                        MetaSettings.putActiveList(context, VerseListFragment.STATE, passage.getMetadata().getInt(DefaultMetaData.STATE));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            tagChipsLayout = (FlowLayout) view.findViewById(R.id.tagChipLayout);

            String[] tags = passage.getTags();
            ArrayList<Integer> tagsList = new ArrayList<>();
            for(String tag : tags) {
                int tagId = (int)db.getTagID(tag);
                tagsList.add(tagId);
            }
            tagsList.add(-10);
            tagAdapter = new TagAdapter(context, tagsList);
            tagChipsLayout.setAdapter(tagAdapter);

            tagChipsLayout.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TagAdapter.ViewHolder vh = (TagAdapter.ViewHolder)v.getTag();
                    if(vh.id == -10) {
                        addNewTag();
                    }
                    else {
                        editTagName(vh.id);
                    }
                }
            });

            tagChipsLayout.setOnItemLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TagAdapter.ViewHolder vh = ((TagAdapter.ViewHolder) v.getTag());

                    if(vh.id != -10) {
                        removeTag(vh.id);
                    }
                    return true;
                }
            });
        }

        db.close();

		setHasOptionsMenu(true);
	}

    private class TagAdapter extends BaseAdapter implements ListAdapter {
        Context context;
        ArrayList<Integer> tags;

        public class ViewHolder {
            int id;
            int position;
            TextView nameText;
            ImageView tagCircle;
            ImageView tagIcon;

            ViewHolder(View inflater) {
                nameText = (TextView) inflater.findViewById(R.id.chip_tag_name);
                tagCircle = (ImageView) inflater.findViewById(R.id.chip_tag_circle);
                tagIcon = (ImageView) inflater.findViewById(R.id.chip_tag_icon);
            }
        }

        public TagAdapter(Context context, ArrayList<Integer> tags) {
            this.context = context;
            this.tags = tags;
        }

        @Override
        public int getCount() {
            return tags.size();
        }

        @Override
        public String getItem(int position) {
            if(position == tags.size() - 1) {
                return "Add New Tag";
            }
            else {
                VerseDB db = new VerseDB(context).open();
                String tagName = db.getTagName(tags.get(position));
                db.close();
                return tagName;
            }
        }

        @Override
        public long getItemId(int position) {
            return tags.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            View view = convertView;
            if(view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_tag_chip, parent, false);
                vh = new ViewHolder(view);
                view.setTag(vh);
            }
            else {
                vh = (ViewHolder) view.getTag();
            }

            //setup bookkeeping information
            vh.nameText.setText(getItem(position));
            vh.id = (int) getItemId(position);
            vh.position = position;

            int color;
            if(position == tags.size() - 1) {
                color = Color.BLACK;
                Drawable circle = Util.Drawables.circle(color);
                vh.tagCircle.setBackgroundDrawable(circle);
                vh.tagIcon.setImageResource(R.drawable.ic_action_add_dark);
            }
            else {
                VerseDB db = new VerseDB(context).open();
                color = db.getTagColor(getItem(position));
                db.close();
                Drawable circle = Util.Drawables.circle(color);
                vh.tagCircle.setBackgroundDrawable(circle);
            }

            return view;
        }

        public void addTag(int tagId) {
            if(!tags.contains(tagId)) {
                tags.add(0, tagId);
                notifyDataSetChanged();
            }
        }

        public void removeTag(int tagId) {
            if(tags.contains(tagId)) {
                tags.remove(Integer.valueOf(tagId));
                notifyDataSetChanged();
            }
        }
    }

//Popups for tags
//------------------------------------------------------------------------------
    private void editTagName(final int tagId) {
        final View view = LayoutInflater.from(context).inflate(R.layout.popup_edit_tag, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        final EditText edit = (EditText) view.findViewById(R.id.edit_text);
        VerseDB db = new VerseDB(context).open();
        edit.setText(db.getTagName(tagId));
        db.close();

        TextView cancelButton = (TextView) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView saveEditButton = (TextView) view.findViewById(R.id.save_edit_button);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edit.getText().toString().trim();
                if(text.length() > 0) {
                    VerseDB db = new VerseDB(context).open();
                    db.updateTag(tagId, text, null);
                    db.close();
                    tagAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void removeTag(final int tagId) {
        final View view = LayoutInflater.from(context).inflate(R.layout.popup_remove_tag, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        TextView cancelButton = (TextView) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView thisVerseButton = (TextView) view.findViewById(R.id.this_verse_button);
        thisVerseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerseDB db = new VerseDB(context).open();
                passage.removeTag(db.getTagName(tagId));
                db.updateVerse(passage);
                db.close();
                tagAdapter.removeTag(tagId);
                tagAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        TextView allVersesButton = (TextView) view.findViewById(R.id.all_verses_button);
        allVersesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerseDB db = new VerseDB(context).open();
                passage.removeTag(db.getTagName(tagId));
                db.updateVerse(passage);
                db.deleteTag(tagId);
                db.close();
                tagAdapter.removeTag(tagId);
                tagAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addNewTag() {
        final View view = LayoutInflater.from(context).inflate(R.layout.popup_new_tag, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        final AutoCompleteTextView edit = (AutoCompleteTextView) view.findViewById(R.id.edit_text);
        VerseDB db = new VerseDB(context).open();

        String[] tagSuggestions = db.getAllTagNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                tagSuggestions
        );
        edit.setAdapter(adapter);

        TextView cancelButton = (TextView) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView addButton = (TextView) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edit.getText().toString().trim();
                if(text.length() > 0) {
                    VerseDB db = new VerseDB(context).open();
                    passage.addTag(text);
                    db.updateVerse(passage);
                    tagAdapter.addTag((int)db.getTagID(text));
                    tagAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
	
//ActionBar
//------------------------------------------------------------------------------


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = ((ActionBarActivity) context).getMenuInflater();
	    inflater.inflate(R.menu.menu_edit_verse, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        VerseDB db = new VerseDB(context).open();

        switch (item.getItemId()) {
	    case R.id.menu_edit_set_notification:
            if(passage != null) {
                MetaSettings.putVerseId(context, passage.getMetadata().getInt(DefaultMetaData.ID));
                MetaSettings.putNotificationActive(context, true);
                MetaSettings.putActiveList(context, listType, listId);
                MainNotification.notify(context).show();
                Toast.makeText(context, passage.getReference().toString() + " set as notification", Toast.LENGTH_SHORT).show();
            }
	    	return true;
	    case R.id.menu_edit_save_changes:
            if(passage != null) {
                passage.getMetadata().putInt(DefaultMetaData.STATE, seekbar.getProgress() + 1);
                passage.setText(editVer.getText().toString());
                db.updateVerse(passage);
                db.close();
                Toast.makeText(context, passage.getReference().toString() + " Updated", Toast.LENGTH_SHORT).show();
                ((ActionBarActivity) context).finish();
            }
	    	return true;
	    case R.id.menu_edit_delete:
            if(passage != null) {
                final View view = LayoutInflater.from(context).inflate(R.layout.popup_delete_verse, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);

                final AlertDialog dialog = builder.create();

                view.findViewById(R.id.verse_list).setVisibility(View.GONE);

                TextView cancelButton = (TextView) view.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                TextView deleteButton = (TextView) view.findViewById(R.id.delete_button);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VerseDB db = new VerseDB(context).open();
                        db.deleteVerse(passage);
                        db.close();
                        dialog.dismiss();

                        Toast.makeText(context, passage.getReference().toString() + " deleted", Toast.LENGTH_SHORT).show();
                        ((ActionBarActivity) context).finish();
                    }
                });

                dialog.show();
            }
	    	return true;
	    case R.id.menu_edit_share:
            if(passage != null) {
                String shareMessage = passage.getReference() + " - " + passage.getText();
                Intent intent = new Intent();
                intent.setType("text/plain");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, passage.getReference().toString());
                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(intent, "Share To..."));
            }
	    	return true;
        default:
            db.close();
            return super.onOptionsItemSelected(item);
	    }
	}
}
