package org.dhis2.usescases.main.program

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import org.dhis2.data.dhislogic.DhisProgramUtils
import org.dhis2.data.dhislogic.DhisTrackedEntityInstanceUtils
import org.dhis2.data.filter.FilterPresenter
import org.dhis2.data.schedulers.TrampolineSchedulerProvider
import org.dhis2.utils.filters.FilterManager
import org.dhis2.utils.resources.ResourceManager
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetInstanceSummary
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

class HomeRepositoryImplTest {

    private lateinit var homeRepository: HomeRepository
    private val d2: D2 = Mockito.mock(D2::class.java, Mockito.RETURNS_DEEP_STUBS)
    private val filterPresenter: FilterPresenter =
        Mockito.mock(FilterPresenter::class.java, Mockito.RETURNS_DEEP_STUBS)
    private val dhisProgramUtils: DhisProgramUtils = mock()
    private val dhisTeiUtils: DhisTrackedEntityInstanceUtils = mock()
    private val scheduler = TrampolineSchedulerProvider()
    private val resourceManager: ResourceManager = mock()

    @Before
    fun setUp() {
        homeRepository = HomeRepositoryImpl(
            d2,
            filterPresenter,
            dhisProgramUtils,
            dhisTeiUtils,
            resourceManager,
            scheduler
        )
        whenever(
            resourceManager.defaultDataSetLabel()
        ) doReturn "dataset"
        whenever(
            resourceManager.defaultEventLabel()
        ) doReturn "event"
        whenever(
            resourceManager.defaultTeiLabel()
        ) doReturn "tei"
        whenever(
            d2.dataSetModule().dataSets().uid(anyString()).blockingGet()
        ) doReturn DataSet.builder()
            .uid("dataSetUid")
            .description("description")
            .style(
                ObjectStyle.builder()
                    .color("color")
                    .icon("icon")
                    .build()
            )
            .access(
                Access.create(
                    true,
                    true,
                    DataAccess.create(
                        true,
                        true
                    )
                )
            )
            .build()
    }

    @After
    fun clear() {
        FilterManager.getInstance().clearAllFilters()
    }

    @Test
    fun `Should return list of data set ProgramViewModel`() {
        whenever(
            filterPresenter.filteredDataSetInstances()
        ) doReturn mock()
        whenever(
            filterPresenter.filteredDataSetInstances().get()
        ) doReturn Single.just(mockedDataSetInstanceSummaries())
        whenever(
            filterPresenter.isAssignedToMeApplied()
        ) doReturn false

        val testObserver = homeRepository.aggregatesModels().test()

        testObserver
            .assertNoErrors()
            .assertValue {
                it.size == 2
            }
    }

    @Test
    fun `Should set data set count to 0 if assign to me is active`() {
        whenever(
            filterPresenter.filteredDataSetInstances()
        ) doReturn mock()
        whenever(
            filterPresenter.filteredDataSetInstances().get()
        ) doReturn Single.just(mockedDataSetInstanceSummaries())
        whenever(
            filterPresenter.isAssignedToMeApplied()
        ) doReturn true
        whenever(
            filterPresenter.areFiltersActive()
        ) doReturn true

        val testObserver = homeRepository.aggregatesModels().test()

        testObserver
            .assertNoErrors()
            .assertValue {
                it.size == 2 &&
                    it[0].count() == 0 &&
                    it[0].translucent() &&
                    it[1].count() == 0 &&
                    it[1].translucent()
            }
    }

    @Test
    fun `Should return list of program ProgramViewModels`() {
        initWheneverForPrograms()
        whenever(
            filterPresenter.areFiltersActive()
        ) doReturn false
        val testOvserver = homeRepository.programModels().test()

        testOvserver
            .assertNoErrors()
            .assertValue {
                it.size == mockedPrograms().size &&
                    it[0].count() == 10 &&
                    it[0].typeName() == "event" &&
                    it[1].count() == 2 &&
                    it[1].hasOverdue() &&
                    it[1].typeName() == "tei"
            }
    }

    private fun initWheneverForPrograms() {
        whenever(
            dhisProgramUtils.getProgramsInCaptureOrgUnits()
        ) doReturn Flowable.just(
            mockedPrograms()
        )
        whenever(
            dhisProgramUtils.getProgramRecordLabel(any(), any(), any())
        ) doReturnConsecutively arrayListOf("event", "tei")
        whenever(
            dhisProgramUtils.getProgramState(any<Program>())
        ) doReturnConsecutively arrayListOf(State.SYNCED, State.TO_POST)
        whenever(
            filterPresenter.filteredEventProgram(any())
        ) doReturn mock()
        whenever(
            filterPresenter.filteredEventProgram(any()).blockingCount()
        ) doReturn 10
        whenever(
            filterPresenter.filteredTrackerProgram(any())
        ) doReturn mock()
        whenever(
            filterPresenter.filteredTrackerProgram(any()).offlineFirst()
        ) doReturn mock()
        whenever(
            filterPresenter.filteredTrackerProgram(any<Program>()).offlineFirst().blockingGet()
        ) doReturn mockedTrackedEntities()

        whenever(
            dhisTeiUtils.hasOverdueInProgram(any(), any())
        ) doReturnConsecutively arrayListOf(false, true)
    }

    private fun mockedDataSetInstanceSummaries(): List<DataSetInstanceSummary> {
        return listOf(
            DataSetInstanceSummary.builder()
                .dataSetUid("dataSetUid_1")
                .dataSetDisplayName("dataSetUid_1")
                .valueCount(5)
                .dataSetInstanceCount(2)
                .state(State.SYNCED)
                .build(),
            DataSetInstanceSummary.builder()
                .dataSetUid("dataSetUid_1")
                .dataSetDisplayName("dataSetUid_1")
                .dataSetInstanceCount(1)
                .valueCount(5)
                .state(State.TO_UPDATE)
                .build()
        )
    }

    private fun mockedPrograms(): List<Program> {
        return arrayListOf(
            Program.builder()
                .uid("program1")
                .displayName("program1")
                .programType(ProgramType.WITHOUT_REGISTRATION)
                .build(),
            Program.builder()
                .uid("program2")
                .displayName("program2")
                .programType(ProgramType.WITH_REGISTRATION)
                .trackedEntityType(
                    TrackedEntityType.builder()
                        .uid("teType")
                        .displayName("Person")
                        .build()
                )
                .build()
        )
    }

    private fun mockedTrackedEntities(): List<TrackedEntityInstance> {
        return arrayListOf(
            TrackedEntityInstance.builder()
                .uid("teiUid1")
                .build(),
            TrackedEntityInstance.builder()
                .uid("teiUid2")
                .build()
        )
    }
}
