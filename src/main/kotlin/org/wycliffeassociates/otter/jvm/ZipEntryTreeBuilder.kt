package org.wycliffeassociates.otter.jvm

import org.wycliffeassociates.otter.common.collections.tree.OtterTree
import org.wycliffeassociates.otter.common.collections.tree.OtterTreeNode
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.IZipEntryTreeBuilder
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.OtterFile
import org.wycliffeassociates.otter.common.domain.resourcecontainer.project.OtterZipFile.Companion.otterFileZ
import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Files
import java.nio.file.SimpleFileVisitor
import java.nio.file.FileSystem
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.util.zip.ZipFile
import java.util.ArrayDeque

object ZipEntryTreeBuilder : IZipEntryTreeBuilder {

    private fun createZipFileSystem(zipFilename: String): FileSystem {
        val path = Paths.get(zipFilename)
        return FileSystems.newFileSystem(path, null)
    }

    override fun buildOtterFileTree(zipFile: ZipFile, projectPath: String): OtterTree<OtterFile> {
        var treeRoot: OtterTree<OtterFile>? = null
        val treeCursor = ArrayDeque<OtterTree<OtterFile>>()

        createZipFileSystem(zipFile.name).use { zipFileSystem ->

            val projectRoot = zipFileSystem.getPath(projectPath)

            Files.walkFileTree(projectRoot, object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun visitFile(file: Path,
                                       attrs: BasicFileAttributes): FileVisitResult {
                    val entry = zipFile.getEntry(file.toString().substringAfter(zipFileSystem.separator))
                    val otterZipFile = otterFileZ(file.toString(), zipFile, zipFileSystem.separator, treeCursor.peek()?.value, entry) // TODO: Is file.toString() correct?
                    treeCursor.peek()?.addChild(OtterTreeNode(otterZipFile))
                    return FileVisitResult.CONTINUE
                }

                @Throws(IOException::class)
                override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                    treeRoot = treeCursor.pop()
                    return FileVisitResult.CONTINUE
                }

                @Throws(IOException::class)
                override fun preVisitDirectory(dir: Path,
                                               attrs: BasicFileAttributes): FileVisitResult {
                    val newDirNode = OtterTree(
                            otterFileZ(dir.toString(), zipFile, zipFileSystem.separator, treeCursor.peek()?.value)
                    )
                    treeCursor.peek()?.addChild(newDirNode)
                    treeCursor.push(newDirNode)
                    return FileVisitResult.CONTINUE
                }
            })
            return treeRoot ?: OtterTree(otterFileZ(zipFile.name, zipFile, zipFileSystem.separator))
        }
    }
}

//    private fun constructZipProjectTree(zipFile: ZipFile, container: ResourceContainer, project: Project)
//            : Pair<ImportResult, Tree> {
//
////        val projectRoot = container.file.resolve(project.path)
////        val collectionKey = container.manifest.dublinCore.identifier
////
////        val list = zipFile.entries().toList()
//
//        //TODO
////        val rootCollection = Collection(0, "en_tn", "tn", "0", null)
////        val projectRoot = OtterFile.Z(OtterZipFile(project.path, zipFile)
//
//        val root = OtterTree<OtterZipFile>(OtterZipFile(zipFile.)) // TODO: Name or path?
//
//        val stack = ArrayDeque<StackNode>()
//
//        val stack = ArrayDeque<Pair<Regex, OtterTree<Any>>>()
//        stack.push(Pair(Regex(".*"), root)) // Create root node that matches everything
//
//        list.forEach { zipEntry ->
//            if (!extensions.matches(zipEntry.name)) {
//                return@forEach // continue
//            }
//            while (!stack.peek().first.matches(zipEntry.name)) {
//                stack.pop()
//            }
//            // Get the remainder of the path that did not match the node regex and split on "/"
//            val parts = stack.peek().first.split(zipEntry.name)[1].split(File.separator)
//            parts.forEach { part ->
//                if (extensions.matches(part)) {
//                    // TODO fileId
//                    stack.peek().second.addAll(
//                            contentNodeList(zipFile.bufferedReaderProvider(zipEntry), 0)
//                    )
//                } else {
//                    val tree = OtterTree<Any>(Collection(0, "test", "tn", "0", null)) // TODO
//                    stack.peek().second.addChild(tree)
//                    stack.push(Pair(Regex(".*"), tree)) // TODO regex
//                }
//            }
//        }
//    }
