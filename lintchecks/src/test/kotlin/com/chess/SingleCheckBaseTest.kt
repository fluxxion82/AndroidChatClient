package com.chess

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestLintResult
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.detector.api.Issue

abstract class SingleCheckBaseTest(
    private val issue: Issue
) {

    protected fun runLint(vararg files: TestFile): TestLintResult =
        lint().files(*files)
            .issues(issue)
            .run()
}
