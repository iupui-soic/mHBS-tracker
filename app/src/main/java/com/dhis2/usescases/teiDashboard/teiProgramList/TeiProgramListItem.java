package com.dhis2.usescases.teiDashboard.teiProgramList;

import android.support.annotation.IntDef;

import org.hisp.dhis.android.core.program.ProgramModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Cristian on 08/03/2018.
 */

public class TeiProgramListItem {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TeiProgramListItemViewType.FIRST_TITLE, TeiProgramListItemViewType.ACTIVE_ENROLLMENT,
            TeiProgramListItemViewType.PROGRAM, TeiProgramListItemViewType.SECOND_TITLE,
            TeiProgramListItemViewType.INACTIVE_ENROLLMENT, TeiProgramListItemViewType.THIRD_TITLE,
            TeiProgramListItemViewType.PROGRAMS_TO_ENROLL})
    public @interface TeiProgramListItemViewType {
        int FIRST_TITLE = 0;
        int ACTIVE_ENROLLMENT = 1;
        int PROGRAM = 2;
        int SECOND_TITLE = 3;
        int INACTIVE_ENROLLMENT = 4;
        int THIRD_TITLE = 5;
        int PROGRAMS_TO_ENROLL = 6;
    }

    private EnrollmentViewModel enrollmentModel;
    private ProgramModel programModel;
    private @TeiProgramListItemViewType
    int viewType;

    public TeiProgramListItem(EnrollmentViewModel enrollmentModel, ProgramModel programModel, int viewType) {
        this.enrollmentModel = enrollmentModel;
        this.programModel = programModel;
        this.viewType = viewType;
    }

    public EnrollmentViewModel getEnrollmentModel() {
        return enrollmentModel;
    }

    public ProgramModel getProgramModel() {
        return programModel;
    }

    public int getViewType() {
        return viewType;
    }
}
