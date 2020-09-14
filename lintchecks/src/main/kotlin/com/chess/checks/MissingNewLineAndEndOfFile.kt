package com.chess.checks

import com.android.ide.common.blame.SourcePosition
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.GradleScanner
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScanner
import org.w3c.dom.Document

class MissingNewLineAndEndOfFile : Detector(), XmlScanner, SourceCodeScanner, GradleScanner {

    override fun visitDocument(context: XmlContext, document: Document) = Unit

    override fun afterCheckFile(context: Context) {
        super.afterCheckFile(context)
        val text = context.file.readBytes()

        if (text.isNotEmpty() && text.last() != '\n'.toByte()) {
            val lineCount = context.file.useLines { it.count() }
            val lastLine = context.file.useLines { it.last() }
            val position = SourcePosition(lineCount, 0, lastLine.length)
            context.report(issue, Location.create(context.file, position), DESCRIPTION)
        }
    }

    companion object {
        private const val ISSUE_ID = "EmptyLineAtEndOfFile"
        private const val DESCRIPTION = "Missing empty line at the end of xml file"

        private val implementation = Implementation(
            MissingNewLineAndEndOfFile::class.java,
            Scope.MANIFEST_AND_RESOURCE_SCOPE
        )

        val issue: Issue = Issue.create(
            ISSUE_ID,
            briefDescription = "Checks whether files end with a line separator",
            explanation = """We just don't like "missing new line" character at the and of github diffs""",
            category = Category.CORRECTNESS,
            priority = 1,
            severity = Severity.ERROR,
            implementation = implementation
        ).addMoreInfo("https://stackoverflow.com/questions/729692/")
    }
}
