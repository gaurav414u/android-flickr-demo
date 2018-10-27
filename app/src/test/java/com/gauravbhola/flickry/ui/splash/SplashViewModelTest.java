package com.gauravbhola.flickry.ui.splash;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import android.os.Handler;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

public class SplashViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    SplashViewModel mSplashViewModel;

    @Before
    public void setup() {
        Application app = new Application();
        mSplashViewModel = new SplashViewModel(app);
    }

    @Test
    public void navigatesToHome() {
        mSplashViewModel.mHandler = mock(Handler.class);
        when(mSplashViewModel.mHandler.postDelayed(any(Runnable.class), anyLong())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable run = invocation.getArgument(0);
                run.run();
                return null;
            }
        });
        Observer ob = mock(Observer.class);
        mSplashViewModel.getNavigationCommand().observeForever(ob);
        mSplashViewModel.splashDisplayed();
        verify(ob).onChanged(null);
    }
}
