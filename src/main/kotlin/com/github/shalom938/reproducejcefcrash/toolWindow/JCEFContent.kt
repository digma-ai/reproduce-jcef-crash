package com.github.shalom938.reproducejcefcrash.toolWindow

import com.intellij.openapi.fileEditor.impl.HTMLEditorProvider
import com.intellij.openapi.project.Project
import org.cef.CefApp
import org.cef.callback.CefCallback
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefResourceHandler
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.awt.BorderLayout
import java.io.IOException
import java.io.InputStream
import javax.swing.JButton
import javax.swing.JPanel
import kotlin.math.min

class JCEFContent(project: Project) : JPanel() {

    init {

        val b = JButton("test")
        b.addActionListener{
            HTMLEditorProvider.openEditor(project, "test", "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "  <head>\n" +
                    "    <meta charset=\"UTF-8\" />\n" +
                    "    <title>JCEF test</title>\n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "    <p>Digma JCEF Test</p>\n" +
                    "  </body>\n" +
                    "</html>")
        }


        layout = BorderLayout()
        add(b,BorderLayout.CENTER)


    }


    private fun registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory(
            "http", "jceftest"
        ) { _, _, _, _ -> MyCefResourceHandler() }
    }
}


class MyCefResourceHandler : CefResourceHandler {


    private var inputStream: InputStream? = null

    override fun processRequest(request: CefRequest?, callback: CefCallback?): Boolean {
        inputStream = javaClass.getResourceAsStream("/jcef/index.html")
        callback!!.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse?, responseLength: IntRef?, redirectUrl: StringRef?) {
        if (inputStream == null) {
            response!!.error = CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND
            response.statusText = "file not found /jcef/index.html"
            response.status = 404
            return
        }

        response!!.status = 200
        response.mimeType = "text/html"
        try {
            responseLength!!.set(inputStream!!.available())
        } catch (e: IOException) {
            response.error = CefLoadHandler.ErrorCode.ERR_ABORTED
            response.statusText = "internal error for /jcef/index.html"
            response.status = 500
        }
    }


    override fun readResponse(
        dataOut: ByteArray,
        bytesToRead: Int,
        bytesRead: IntRef?,
        callback: CefCallback?
    ): Boolean {
        return try {
            val available = inputStream!!.available()
            if (available == 0) {
                bytesRead!!.set(0)
                inputStream!!.close()
                return false
            }
            val toRead = min(available, bytesToRead)
            val read = inputStream!!.read(dataOut, 0, toRead)
            bytesRead!!.set(read)
            true
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun cancel() {
        try {
            if (inputStream != null) {
                inputStream!!.close()
            }
        } catch (e: IOException) {
            //ignore
        }
    }

}