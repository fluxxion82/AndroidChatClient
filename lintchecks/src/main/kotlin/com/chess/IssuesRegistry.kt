package com.chess

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.chess.checks.MissingNewLineAndEndOfFile

class IssuesRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(
            MissingNewLineAndEndOfFile.issue
        )

    override val api: Int
        get() = CURRENT_API
}
