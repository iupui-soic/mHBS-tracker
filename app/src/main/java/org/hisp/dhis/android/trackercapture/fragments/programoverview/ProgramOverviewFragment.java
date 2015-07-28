/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.trackercapture.fragments.programoverview;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.raizlabs.android.dbflow.structure.Model;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.activities.INavigationHandler;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.fragments.SettingsFragment;
import org.hisp.dhis.android.sdk.fragments.dataentry.DataEntryFragment;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.utils.ui.views.FloatingActionButton;
import org.hisp.dhis.android.sdk.utils.ui.views.FontTextView;
import org.hisp.dhis.android.trackercapture.R;
import org.hisp.dhis.android.trackercapture.fragments.enrollment.EnrollmentFragment;
import org.hisp.dhis.android.trackercapture.fragments.enrollmentdate.EnrollmentDateFragment;
import org.hisp.dhis.android.trackercapture.fragments.programoverview.registerRelationshipDialogFragment.RegisterRelationshipDialogFragment;
import org.hisp.dhis.android.trackercapture.fragments.trackedentityinstanceprofile.TrackedEntityInstanceProfileFragment;
import org.hisp.dhis.android.trackercapture.fragments.upcomingevents.ProgramDialogFragment;
import org.hisp.dhis.android.trackercapture.ui.adapters.ProgramAdapter;
import org.hisp.dhis.android.trackercapture.ui.adapters.ProgramStageAdapter;
import org.hisp.dhis.android.trackercapture.ui.rows.programoverview.OnProgramStageEventClick;
import org.hisp.dhis.android.trackercapture.ui.rows.programoverview.ProgramStageEventRow;
import org.hisp.dhis.android.trackercapture.ui.rows.programoverview.ProgramStageLabelRow;
import org.hisp.dhis.android.trackercapture.ui.rows.programoverview.ProgramStageRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
public class ProgramOverviewFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        ProgramDialogFragment.OnOptionSelectedListener,
        LoaderManager.LoaderCallbacks<ProgramOverviewFragmentForm>, AdapterView.OnItemSelectedListener {

    public static final String CLASS_TAG = ProgramOverviewFragment.class.getSimpleName();
    private static final String STATE = "state:UpcomingEventsFragment";
    private static final int LOADER_ID = 578922;

    private static final String EXTRA_ARGUMENTS = "extra:Arguments";
    private static final String EXTRA_SAVED_INSTANCE_STATE = "extra:savedInstanceState";

    private static final String ORG_UNIT_ID = "extra:orgUnitId";
    private static final String PROGRAM_ID = "extra:ProgramId";
    private static final String TRACKEDENTITYINSTANCE_ID = "extra:TrackedEntityInstanceId";
    private String errorMessage;

    private ListView listView;
    private ProgressBar mProgressBar;
    private ProgramStageAdapter adapter;

    private View mSpinnerContainer;
    private Spinner mSpinner;
    private ProgramAdapter mSpinnerAdapter;

    private LinearLayout enrollmentLayout;
    private TextView enrollmentDateLabel;
    private TextView enrollmentDateValue;
    private TextView incidentDateLabel;
    private TextView incidentDateValue;

    private LinearLayout missingEnrollmentLayout;
    private FloatingActionButton newEnrollmentButton;

    private CardView profileCardView;
    private CardView enrollmentCardview;

    private ImageButton followupButton;
    private ImageButton profileButton;
    private ImageView enrollmentServerStatus;
    private Button completeButton;
    private Button terminateButton;

    private TextView attribute1Label;
    private TextView attribute1Value;
    private TextView attribute2Label;
    private TextView attribute2Value;

    private LinearLayout relationshipsLinearLayout;
    private Button newRelationshipButton;

    private ProgramOverviewFragmentState mState;
    private ProgramOverviewFragmentForm mForm;

    private INavigationHandler mNavigationHandler;

    public static ProgramOverviewFragment newInstance(String orgUnitId, String programId, long trackedEntityInstanceId) {
        ProgramOverviewFragment fragment = new ProgramOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, orgUnitId);
        args.putString(PROGRAM_ID, programId);
        args.putLong(TRACKEDENTITYINSTANCE_ID, trackedEntityInstanceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putBundle(EXTRA_ARGUMENTS, getArguments());
        argumentsBundle.putBundle(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, argumentsBundle, this);

        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity) {
            getActionBar().setDisplayShowTitleEnabled(false);
        }

        if (activity instanceof INavigationHandler) {
            mNavigationHandler = (INavigationHandler) activity;
        } else {
            throw new IllegalArgumentException("Activity must implement INavigationHandler interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity() != null &&
                getActivity() instanceof AppCompatActivity) {
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setHomeButtonEnabled(false);
        }

        // we need to nullify reference
        // to parent activity in order not to leak it
        if (getActivity() != null &&
                getActivity() instanceof INavigationHandler) {
            ((INavigationHandler) getActivity()).setBackPressedListener(null);
        }
        // we need to nullify reference
        // to parent activity in order not to leak it
        mNavigationHandler = null;
    }

    @Override
    public void onDestroyView() {
        detachSpinner();
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_programoverview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        listView = (ListView) view.findViewById(R.id.listview);
        View header = getLayoutInflater(savedInstanceState).inflate(
                R.layout.fragment_programoverview_header, listView, false
        );

        relationshipsLinearLayout = (LinearLayout) header.findViewById(R.id.relationships_linearlayout);
        newRelationshipButton = (Button) header.findViewById(R.id.addrelationshipbutton);
        newRelationshipButton.setOnClickListener(this);

        mProgressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        adapter = new ProgramStageAdapter(getLayoutInflater(savedInstanceState));
        listView.addHeaderView(header, CLASS_TAG, false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        enrollmentServerStatus = (ImageView) header.findViewById(R.id.enrollmentstatus);
        enrollmentLayout = (LinearLayout) header.findViewById(R.id.enrollmentLayout);
        enrollmentDateLabel = (TextView) header.findViewById(R.id.dateOfEnrollmentLabel);
        enrollmentDateValue = (TextView) header.findViewById(R.id.dateOfEnrollmentValue);
        incidentDateLabel = (TextView) header.findViewById(R.id.dateOfIncidentLabel);
        incidentDateValue = (TextView) header.findViewById(R.id.dateOfIncidentValue);
        profileCardView = (CardView) header.findViewById(R.id.profile_cardview);
        enrollmentCardview = (CardView) header.findViewById(R.id.enrollment_cardview);

        completeButton = (Button) header.findViewById(R.id.complete);
        terminateButton = (Button) header.findViewById(R.id.terminate);
        followupButton = (ImageButton) header.findViewById(R.id.followupButton);
        profileButton = (ImageButton) header.findViewById(R.id.profile_button);
        completeButton.setOnClickListener(this);
        terminateButton.setOnClickListener(this);
        followupButton.setOnClickListener(this);
        followupButton.setVisibility(View.GONE);
        profileButton.setOnClickListener(this);
        profileCardView.setOnClickListener(this);
        enrollmentServerStatus.setOnClickListener(this);
        enrollmentLayout.setOnClickListener(this);


        missingEnrollmentLayout = (LinearLayout) header.findViewById(R.id.missingenrollmentlayout);
        newEnrollmentButton = (FloatingActionButton) header.findViewById(R.id.newenrollmentbutton);
        newEnrollmentButton.setOnClickListener(this);

        attribute1Label = (TextView) header.findViewById(R.id.headerItem1label);
        attribute1Value = (TextView) header.findViewById(R.id.headerItem1value);
        attribute2Label = (TextView) header.findViewById(R.id.headerItem2label);
        attribute2Value = (TextView) header.findViewById(R.id.headerItem2value);

        Bundle fragmentArguments = getArguments();
        Log.d(CLASS_TAG, "program: " + fragmentArguments.getString(PROGRAM_ID));

        attachSpinner();
        mSpinnerAdapter.swapData(MetaDataController.getProgramsForOrganisationUnit
                (fragmentArguments.getString(ORG_UNIT_ID),
                        Program.SINGLE_EVENT_WITH_REGISTRATION,
                        Program.MULTIPLE_EVENTS_WITH_REGISTRATION,
                        Program.WITH_REGISTRATION));

        if (savedInstanceState != null &&
                savedInstanceState.getParcelable(STATE) != null) {
            mState = savedInstanceState.getParcelable(STATE);
        }

        if (mState == null) {
            mState = new ProgramOverviewFragmentState();
            OrganisationUnit ou = MetaDataController.getOrganisationUnit(fragmentArguments.getString(ORG_UNIT_ID));
            Program program = MetaDataController.getProgram(fragmentArguments.getString(PROGRAM_ID));
            mState.setOrgUnit(ou.getId(), ou.getLabel());
            mState.setProgram(program.getId(), program.getName());
            mState.setTrackedEntityInstance(fragmentArguments.getLong(TRACKEDENTITYINSTANCE_ID, -1));
        }

        onRestoreState(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.getEventBus().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_upcoming_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            mNavigationHandler.switchFragment(
                    new SettingsFragment(), SettingsFragment.TAG, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onRestoreState(boolean hasPrograms) {

        ProgramOverviewFragmentState backedUpState = new ProgramOverviewFragmentState(mState);
        if (!backedUpState.isProgramEmpty()) {
            onProgramSelected(
                    backedUpState.getProgramId(),
                    backedUpState.getProgramName()
            );
        } else {
            //todo
        }
    }

    private ActionBar getActionBar() {
        if (getActivity() != null &&
                getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) getActivity()).getSupportActionBar();
        } else {
            throw new IllegalArgumentException("Fragment should be attached to ActionBarActivity");
        }
    }

    private Toolbar getActionBarToolbar() {
        if (isAdded() && getActivity() != null ) {
            return (Toolbar) getActivity().findViewById(R.id.toolbar1);
        } else {
            throw new IllegalArgumentException("Fragment should be attached to MainActivity");
        }
    }

    private void attachSpinner() {
        if (!isSpinnerAttached()) {
            Toolbar toolbar = getActionBarToolbar();

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            mSpinnerContainer = inflater.inflate(
                    org.hisp.dhis.android.sdk.R.layout.toolbar_spinner_simple, toolbar, false);

            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            toolbar.addView(mSpinnerContainer, lp);

            mSpinnerAdapter = new ProgramAdapter(inflater);

            mSpinner = (Spinner) mSpinnerContainer.findViewById(org.hisp.dhis.android.sdk.R.id.toolbar_spinner);
            mSpinner.setAdapter(mSpinnerAdapter);
            mSpinner.setOnItemSelectedListener(this);
        }
    }

    private void detachSpinner() {
        if (isSpinnerAttached()) {
            if (mSpinnerContainer != null) {
                ((ViewGroup) mSpinnerContainer.getParent()).removeView(mSpinnerContainer);
                mSpinnerContainer = null;
                mSpinner = null;
                if (mSpinnerAdapter != null) {
                    mSpinnerAdapter.swapData(null);
                    mSpinnerAdapter = null;
                }
            }
        }
    }

    private boolean isSpinnerAttached() {
        return mSpinnerContainer != null;
    }

    public void onProgramSelected(String programId, String programName) {

        mState.setProgram(programId, programName);
        clearViews();
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<ProgramOverviewFragmentForm> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            modelsToTrack.add(Event.class);
            modelsToTrack.add(Enrollment.class);
            modelsToTrack.add(TrackedEntityInstance.class);
            modelsToTrack.add(TrackedEntityAttributeValue.class);
            modelsToTrack.add(Relationship.class);
            modelsToTrack.add(FailedItem.class);
            return new DbLoader<>(
                    getActivity().getBaseContext(), modelsToTrack,
                    new ProgramOverviewFragmentQuery(args.getString(PROGRAM_ID),
                            args.getLong(TRACKEDENTITYINSTANCE_ID, -1)));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ProgramOverviewFragmentForm> loader, ProgramOverviewFragmentForm data) {
        if (LOADER_ID == loader.getId()) {
            mForm = data;
            mProgressBar.setVisibility(View.GONE);
            if(mForm == null || mForm.getEnrollment() == null) {
                showNoActiveEnrollment();
                return;
            } else {
                enrollmentLayout.setVisibility(View.VISIBLE);
                missingEnrollmentLayout.setVisibility(View.GONE);
            }
            enrollmentDateLabel.setText(data.getDateOfEnrollmentLabel());
            enrollmentDateValue.setText(data.getDateOfEnrollmentValue());
            incidentDateLabel.setText(data.getIncidentDateLabel());
            incidentDateValue.setText(data.getIncidentDateValue());

            if(DataValueController.getFailedItem(FailedItem.ENROLLMENT, mForm.getEnrollment().getLocalId()) != null) {
                enrollmentServerStatus.setImageResource(R.drawable.ic_event_error);
            } else if(!mForm.getEnrollment().isFromServer()) {
                enrollmentServerStatus.setImageResource(R.drawable.ic_offline);
            } else {
                enrollmentServerStatus.setImageResource(R.drawable.ic_from_server);
            }

            if(mForm.getEnrollment().getStatus().equals(Enrollment.COMPLETED)) {
                setCompleted();
            }

            if(mForm.getEnrollment().getStatus().equals(Enrollment.CANCELLED)) {
                setTerminated();
            }

            if(mForm.getEnrollment().getFollowup()) {
                setFollowupButton(true);
            }

            if(data.getAttribute1Label() == null || data.getAttribute1Value() == null) {
                attribute1Label.setVisibility(View.GONE);
                attribute1Value.setVisibility(View.GONE);
            } else {
                attribute1Label.setText(data.getAttribute1Label());
                attribute1Value.setText(data.getAttribute1Value());
            }

            if(data.getAttribute2Label() == null || data.getAttribute2Value() == null) {
                attribute2Label.setVisibility(View.GONE);
                attribute2Value.setVisibility(View.GONE);
            } else {
                attribute2Label.setText(data.getAttribute2Label());
                attribute2Value.setText(data.getAttribute2Value());
            }

            final Map<Long,FailedItem> failedEvents = getFailedEvents();
            boolean generateNextVisit = false;

            for(ProgramStageRow row: data.getProgramStageRows()) {
                if(row instanceof ProgramStageLabelRow) {
                    ProgramStageLabelRow stageRow = (ProgramStageLabelRow) row;
                    if(stageRow.getProgramStage().getRepeatable()) {
                        stageRow.setButtonListener(this);
                    }

                    if(generateNextVisit)
                    {
                        int stageCount = 0;

                        if(stageRow.getEventRows() != null)
                        {
                            stageCount = stageRow.getEventRows().size();
                        }
                        if(stageCount < 1 || stageRow.getProgramStage().getRepeatable()) // should only be able to add more stages if stage is repeatable
                            stageRow.setButtonListener(this);

                        generateNextVisit = false;
                    }

                    if(stageRow.getProgramStage().getAllowGenerateNextVisit())
                    {
                        if(stageRow.getEventRows() != null)
                        {
                            for(ProgramStageEventRow eventRow : stageRow.getEventRows())
                            {
                                if(eventRow.getEvent().getStatus().equals(Event.STATUS_COMPLETED))
                                    generateNextVisit = true;
                            }
                        }
                    }
                }
                else if(row instanceof ProgramStageEventRow)
                {
                    final ProgramStageEventRow eventRow = (ProgramStageEventRow) row;

                    if(DataValueController.getFailedItem(FailedItem.EVENT, eventRow.getEvent().getLocalId())!=null)
                    {
                        eventRow.setHasFailed(true);
                        eventRow.setMessage(failedEvents.get(eventRow.getEvent().getLocalId()).getErrorMessage());
                    }
                    else if(eventRow.getEvent().isFromServer())
                    {
                        eventRow.setSynchronized(true);
                        eventRow.setMessage(getString(R.string.status_sent_description));
                    }
                    else
                    {
                        eventRow.setSynchronized(false);
                        eventRow.setMessage(getString(R.string.status_offline_description));
                    }
                }
            }
            setRelationships(getLayoutInflater(getArguments().getBundle(EXTRA_SAVED_INSTANCE_STATE)));
            adapter.swapData(data.getProgramStageRows());
        }
    }

    /**
     * Inflates views and adds them to linear layout for relationships, sort of like a listview, but
     * inside another listview
     */
    public void setRelationships(LayoutInflater inflater) {
        relationshipsLinearLayout.removeAllViews();
        if(mForm.getTrackedEntityInstance() != null && mForm.getTrackedEntityInstance().getRelationships()!=null) {
            for(Relationship relationship: mForm.getTrackedEntityInstance().getRelationships()) {
                if(relationship==null) {
                    continue;
                }
                LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.listview_row_relationship, null);
                FontTextView currentTeiRelationshipLabel = (FontTextView) ll.findViewById(R.id.current_tei_relationship_label);
                FontTextView relativeLabel = (FontTextView) ll.findViewById(R.id.relative_relationship_label);
                RelationshipType relationshipType = MetaDataController.getRelationshipType(relationship.getRelationship());

                if(relationshipType!=null) {

                    /* establishing if the relative is A or B in Relationship Type */
                    final TrackedEntityInstance relative;
                    if(mForm.getTrackedEntityInstance().getTrackedEntityInstance() != null &&
                            mForm.getTrackedEntityInstance().getTrackedEntityInstance().equals(relationship.getTrackedEntityInstanceA())) {

                        currentTeiRelationshipLabel.setText(relationshipType.getaIsToB());
                        relative = DataValueController.getTrackedEntityInstance(relationship.getTrackedEntityInstanceB());

                    } else if(mForm.getTrackedEntityInstance().getTrackedEntityInstance() != null &&
                            mForm.getTrackedEntityInstance().getTrackedEntityInstance().equals(relationship.getTrackedEntityInstanceB())) {

                        currentTeiRelationshipLabel.setText(relationshipType.getbIsToA());
                        relative = DataValueController.getTrackedEntityInstance(relationship.getTrackedEntityInstanceA());
                    } else {
                        continue;
                    }

                    /* Creating a string to display as name of relative from attributes */
                    String relativeString = "";
                    if(relative != null && relative.getAttributes() != null) {List<Enrollment> enrollments = DataValueController.getEnrollments(relative);
                        List<TrackedEntityAttribute> attributesToShow = new ArrayList<>();
                        if(enrollments!=null && !enrollments.isEmpty()) {
                            Program program = null;
                            for (Enrollment e : enrollments) {
                                if (e != null && e.getProgram() != null && e.getProgram().getProgramTrackedEntityAttributes() != null) {
                                    program = e.getProgram();
                                    break;
                                }
                            }
                            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = program.getProgramTrackedEntityAttributes();
                            for (int i = 0; i < programTrackedEntityAttributes.size() && i < 2; i++) {
                                attributesToShow.add(programTrackedEntityAttributes.get(i).getTrackedEntityAttribute());
                            }
                            for (int i = 0; i < attributesToShow.size() && i < 2; i++) {
                                TrackedEntityAttributeValue av = DataValueController.getTrackedEntityAttributeValue(attributesToShow.get(i).getId(), relative.getLocalId());
                                if (av != null && av.getValue() != null) {
                                    relativeString += av.getValue() + " ";
                                }
                            }
                        } else {
                            for(int i = 0; i<relative.getAttributes().size() && i<2; i++) {
                                if(relative.getAttributes().get(i) != null && relative.getAttributes().get(i).getValue() != null) {
                                    relativeString += relative.getAttributes().get(i).getValue() + " ";
                                }
                            }
                        }
                    }
                    if(relativeString.isEmpty()) {
                        relativeString = getString(R.string.unknown);
                    }
                    relativeLabel.setText(relativeString);

                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(relative != null) {
                                ProgramOverviewFragment fragment = ProgramOverviewFragment.
                                        newInstance(getArguments().getString(ORG_UNIT_ID),
                                                getArguments().getString(PROGRAM_ID), relative.getLocalId());
                                mNavigationHandler.switchFragment(fragment, CLASS_TAG, true);
                            }
                        }
                    });
                    relationshipsLinearLayout.addView(ll);
                }
            }
        }
    }

    @Subscribe
    public void onItemClick(OnProgramStageEventClick eventClick)
    {
        if(eventClick.isHasPressedFailedButton()) {
            Dhis2.showStatusDialog(getChildFragmentManager(), eventClick.getItem());
        }
        else
        {
            showDataEntryFragment(eventClick.getEvent(), eventClick.getEvent().getProgramStageId());
        }
    }

    public Map<Long, FailedItem> getFailedEvents()
    {
        Map<Long, FailedItem> failedItemMap = new HashMap<>();
        List<FailedItem> failedItems = DataValueController.getFailedItems();
        if(failedItems != null && failedItems.size() > 0)
        {
            for(FailedItem failedItem : failedItems)
            {
                if(failedItem.getItemType().equals(FailedItem.EVENT))
                    failedItemMap.put(failedItem.getItemId(),failedItem);
            }
        }
        return failedItemMap;
    }

    public void showNoActiveEnrollment() {
        enrollmentLayout.setVisibility(View.GONE);
        missingEnrollmentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ProgramOverviewFragmentForm> loader) {
        clearViews();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProgramStageRow row = (ProgramStageRow) listView.getItemAtPosition(position);
        if(row instanceof ProgramStageEventRow) {
            ProgramStageEventRow eventRow = (ProgramStageEventRow) row;
            Event event = eventRow.getEvent();
            showDataEntryFragment(event, event.getProgramStageId());
        }
    }

    public void enroll() {
        EnrollmentFragment enrollmentFragment = EnrollmentFragment.newInstance(mState.getOrgUnitId(), mState.getProgramId(), mState.getTrackedEntityInstanceId());
        mNavigationHandler.switchFragment(enrollmentFragment, EnrollmentFragment.class.getName(), true);
    }

    public void showDataEntryFragment(Event event, String programStage) {
        Bundle args = getArguments();
        DataEntryFragment fragment;
        if(event == null) {
            fragment = DataEntryFragment.newInstanceWithEnrollment(args.getString(ORG_UNIT_ID), args.getString(PROGRAM_ID), programStage, mForm.getEnrollment().getLocalId());
        } else {
            fragment = DataEntryFragment.newInstanceWithEnrollment(args.getString(ORG_UNIT_ID), args.getString(PROGRAM_ID), programStage,
                    event.getLocalEnrollmentId(), event.getLocalId());
        }

        mNavigationHandler.switchFragment(fragment, ProgramOverviewFragment.CLASS_TAG, true);
    }

    public void completeEnrollment() {
        mForm.getEnrollment().setStatus(Enrollment.COMPLETED);
        mForm.getEnrollment().setFromServer(false);
        mForm.getEnrollment().async().save();
        setCompleted();
        clearViews();
    }

    /**
     * Disables the ability to edit enrollment info
     * Program stages can still be viewed but not changed.
     */
    public void setCompleted() {
        completeButton.setEnabled(false);
        completeButton.setAlpha(0x40);
        terminateButton.setEnabled(false);
        terminateButton.setAlpha(0x40);
        followupButton.setEnabled(false);
        followupButton.setAlpha(0x40);
    }

    public void terminateEnrollment() {
        mForm.getEnrollment().setStatus(Enrollment.CANCELLED);
        mForm.getEnrollment().setFromServer(false);
        mForm.getEnrollment().async().save();
        setTerminated();
        clearViews();
    }

    /**
     * Removes the currently selected enrollment from being currently selected
     */
    public void setTerminated() {
        onProgramSelected(mForm.getProgram().getId(), mForm.getProgram().getName());
    }

    public void toggleFollowup() {
        if(mForm==null || mForm.getEnrollment()==null) return;
        mForm.getEnrollment().setFollowup(!mForm.getEnrollment().getFollowup());
        mForm.getEnrollment().setFromServer(false);
        mForm.getEnrollment().async().save();
        setFollowupButton(mForm.getEnrollment().getFollowup());
    }

    public void setFollowupButton(boolean enabled) {
        if(followupButton==null) return;
        if(enabled) {
            followupButton.setBackgroundResource(R.drawable.rounded_imagebutton_red);
        } else {
            followupButton.setBackgroundResource(R.drawable.rounded_imagebutton_gray);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.neweventbutton: {
                if(mForm.getEnrollment().getStatus().equals(Enrollment.ACTIVE)) {
                    ProgramStage programStage = (ProgramStage) view.getTag();
                    showDataEntryFragment(null, programStage.getId());
                }
                break;
            }

            case R.id.eventbackground: {
                if(mForm.getEnrollment().getStatus().equals(Enrollment.ACTIVE))
                {
                    Event event = (Event) view.getTag();
                    showDataEntryFragment(event, event.getProgramStageId());
                }
                break;
            }

            case R.id.complete: {
                Dhis2.showConfirmDialog(getActivity(),
                        getString(R.string.complete),
                        getString(R.string.confirm_complete_enrollment),
                        getString(R.string.complete),
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                completeEnrollment();
                            }
                        });
                break;
            }

            case R.id.terminate: {
                Dhis2.showConfirmDialog(getActivity(),
                        getString(R.string.terminate),
                        getString(R.string.confirm_terminate_enrollment),
                        getString(R.string.terminate),
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                terminateEnrollment();
                            }
                        });
                break;
            }

            case R.id.followupButton: {
                toggleFollowup();
                break;
            }

            case R.id.newenrollmentbutton: {
                enroll();
                break;
            }

            case R.id.profile_cardview: {
                editTrackedEntityInstanceProfile();
                break;
            }
            case R.id.profile_button: {
                editTrackedEntityInstanceProfile();
                break;
            }
            case R.id.enrollmentstatus: {
                Dhis2.showStatusDialog(getChildFragmentManager(), mForm.getEnrollment());
                break;
            }
            case R.id.addrelationshipbutton: {
                showAddRelationshipFragment();
                break;
            }
            case R.id.enrollmentLayout:
            {
                editEnrollmentDates();
            }
        }
    }

    private void clearViews() {
        adapter.swapData(null);
    }

    private void editEnrollmentDates()
    {
        EnrollmentDateFragment fragment = EnrollmentDateFragment.newInstance(mForm.getEnrollment().getLocalId());
        mNavigationHandler.switchFragment(fragment, EnrollmentDateFragment.TAG, true);
    }
    private void editTrackedEntityInstanceProfile()
    {
        TrackedEntityInstanceProfileFragment fragment = TrackedEntityInstanceProfileFragment.newInstance(getArguments().
                getLong(TRACKEDENTITYINSTANCE_ID), getArguments().getString(PROGRAM_ID));
        mNavigationHandler.switchFragment(fragment, TrackedEntityInstanceProfileFragment.TAG, true);
    }

    private void showAddRelationshipFragment() {
        RegisterRelationshipDialogFragment fragment = RegisterRelationshipDialogFragment.newInstance(mForm.getTrackedEntityInstance().getLocalId());
        fragment.show(getChildFragmentManager(), CLASS_TAG);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Program program = (Program) mSpinnerAdapter.getItem(position);
        onProgramSelected(program.getId(), program.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onOptionSelected(int dialogId, int position, String id, String name) {
        switch (dialogId) {

            case ProgramDialogFragment.ID: {
                onProgramSelected(id, name);
                break;
            }
        }
    }
}
