package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RAHUL AI", appName)
  }

  @Test
  fun `test heart particles preference default and toggle`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.repository.PreferencesRepository(context)
    assertEquals(true, repo.settings.value.isHeartParticlesEnabled)

    repo.setHeartParticlesEnabled(false)
    assertEquals(false, repo.settings.value.isHeartParticlesEnabled)

    repo.setHeartParticlesEnabled(true)
    assertEquals(true, repo.settings.value.isHeartParticlesEnabled)
  }
}
