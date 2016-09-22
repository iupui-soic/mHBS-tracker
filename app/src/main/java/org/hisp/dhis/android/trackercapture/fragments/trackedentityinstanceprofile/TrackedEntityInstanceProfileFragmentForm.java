/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.trackercapture.fragments.trackedentityinstanceprofile;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;

import java.util.List;
import java.util.Map;

/**
 * Created by erling on 5/18/15.
 */
public class TrackedEntityInstanceProfileFragmentForm
{
    private Enrollment mEnrollment;
    private Program mProgram;
    private TrackedEntityInstance mTrackedEntityInstance;
    private List<Row> mDataEntryRows;
    private List<TrackedEntityAttributeValue> trackedEntityAttributeValues;
    //for the program rules:
    private Map<String, TrackedEntityAttributeValue> trackedEntityAttributeValueMap; //value storage
    private boolean outOfTrackedEntityAttributeGeneratedValues; //?

    public List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues() {
        return trackedEntityAttributeValues;
    }

    public void setTrackedEntityAttributeValues(List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        this.trackedEntityAttributeValues = trackedEntityAttributeValues;
    }

    public Enrollment getEnrollment() {
        return mEnrollment;
    }

    public void setEnrollment(Enrollment mEnrollment) {
        this.mEnrollment = mEnrollment;
    }

    public Program getProgram() {
        return mProgram;
    }

    public void setProgram(Program mProgram) {
        this.mProgram = mProgram;
    }

    public TrackedEntityInstance getTrackedEntityInstance() {
        return mTrackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance mTrackedEntityInstance) {
        this.mTrackedEntityInstance = mTrackedEntityInstance;
    }

    public List<Row> getDataEntryRows() {
        return mDataEntryRows;
    }

    public void setDataEntryRows(List<Row> mDataEntryRows) {
        this.mDataEntryRows = mDataEntryRows;
    }
    public Map<String, TrackedEntityAttributeValue> getTrackedEntityAttributeValueMap() {
        return trackedEntityAttributeValueMap;
    }

    public void setTrackedEntityAttributeValueMap(Map<String, TrackedEntityAttributeValue> trackedEntityAttributeValueMap) {
        this.trackedEntityAttributeValueMap = trackedEntityAttributeValueMap;
    }

    public void setOutOfTrackedEntityAttributeGeneratedValues(boolean outOfTrackedEntityAttributeGeneratedValues) {
        this.outOfTrackedEntityAttributeGeneratedValues = outOfTrackedEntityAttributeGeneratedValues;
    }

    public boolean isOutOfTrackedEntityAttributeGeneratedValues() {
        return outOfTrackedEntityAttributeGeneratedValues;
    }

}
