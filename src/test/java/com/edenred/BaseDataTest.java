package com.edenred;

import com.edenred.data.DataCleanupTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS,
        value = {DataCleanupTestExecutionListener.class})
public class BaseDataTest {
}
