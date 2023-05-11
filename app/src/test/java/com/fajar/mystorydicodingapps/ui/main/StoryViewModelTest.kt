package com.fajar.mystorydicodingapps.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.fajar.mystorydicodingapps.DummyData
import com.fajar.mystorydicodingapps.MainDispatcherRule
import com.fajar.mystorydicodingapps.StoryRepository
import com.fajar.mystorydicodingapps.adapter.ListStoryAdapter
import com.fajar.mystorydicodingapps.network.story.StoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runBlockingTest {
        val dummyStory = DummyData.generateDummyStoryItem()
        val dummyToken = DummyData.generateDummyToken()
        val data: PagingData<StoryItem> = PagingData.from(dummyStory)
        Mockito.`when`(storyRepository.getsAllStories(dummyToken)).thenReturn(flowOf(data))

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<StoryItem> = storyViewModel.getAllStories(dummyToken).first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runBlockingTest {
        val dummyToken = DummyData.generateDummyToken()
        val data: PagingData<StoryItem> = PagingData.empty()
        Mockito.`when`(storyRepository.getsAllStories(dummyToken)).thenReturn(flowOf(data))

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<StoryItem> = storyViewModel.getAllStories(dummyToken).first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}

    override fun onRemoved(position: Int, count: Int) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    override fun onChanged(position: Int, count: Int, payload: Any?) {}

}







