package com.github.shalom938.reproducejcefcrash.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.shalom938.reproducejcefcrash.MyBundle
import com.github.shalom938.reproducejcefcrash.services.MyProjectService
import javax.swing.JButton


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val jcefPanel = JCEFContent()
        val jcefContent = ContentFactory.getInstance().createContent(jcefPanel, "JCEFPanel", false)
        toolWindow.contentManager.addContent(jcefContent)


    }

    override fun shouldBeAvailable(project: Project) = true
}
