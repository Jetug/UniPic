package com.example.unipicdev.models

import java.io.File

class DataSaver() {

    private val programDirPath = "/storage/emulated/0/UniPic/"
    private val saveFilePath = "Dirs.txt"
    private val programDir : File
    get() = createProgramDir()
    private val saveFile: File
    get() = createSaveFile()

//    fun saveDir(path: ArrayList<File>){
//        path.forEach{
//            writeToFile(saveFile, it)
//        }
//    }
//
//    @JvmName("saveDir1")

    init {
        createSaveFile()
    }

    fun saveDir(path: Collection<String>){
        path.forEach{
            writeToFile(it)
        }
    }

    fun saveDir(path: String){
        writeToFile(path)
    }



    fun getSavedDirs(onFind: (file: File) -> Unit)/*: ArrayList<File>*/{
        //val paths = ArrayList<File>()
        saveFile.bufferedReader().forEachLine {
            val file = File(it)
            if(exists(file)){
                onFind(file)
                //paths.add(file)
            }
        }
        //return paths
    }

    fun getSavedDirs(): ArrayList<File>{
        createSaveFile()
        val paths = ArrayList<File>()
        val ex = saveFile.exists()
        if(ex) {
            saveFile.bufferedReader().forEachLine {
                val file = File(it)
                if (exists(file)) {
                    paths.add(file)
                }
            }
        }
        return paths
    }

    fun getNotHiddenSavedDirs(): ArrayList<File>{
        createSaveFile()
        val paths = ArrayList<File>()
        if(saveFile.exists()) {
            saveFile.bufferedReader().forEachLine {
                val file = File(it)
                if (exists(file)) {
                    paths.add(file)
                }
            }
        }
        return paths
    }

    fun createImagePositionsFile(){

    }

    fun saveImagePositions(directoryPath: String, files:MutableList<ThumbnailModel>){
        val sortingFileName = "Sort.txt"
        val sortingFile = File(File(directoryPath), sortingFileName)

        if (!sortingFile.exists()) {
            sortingFile.createNewFile()
        }

        //CoroutineScope(Dispatchers.IO).launch{
            var text = ""
            for (file in files) {
                text += file.file.name
                text += "\n"
            }
            sortingFile.printWriter().use {
                it.println(text)
           }
        //}
    }

    fun getCustomMediaList(directoryPath: String):MutableList<ThumbnailModel>{
        val sortingFileName = "Sort.txt"
        val sortingFile = File(File(directoryPath),sortingFileName)
        val files = mutableListOf<ThumbnailModel>()

        if (sortingFile.exists()) {
            sortingFile.bufferedReader().forEachLine {
                if (it != "") {
                    val file = File(it)
                    files.add(ThumbnailModel(file))
                }
            }
        }
        return files
    }

    fun getCustomMediaListF(directoryPath: String):MutableList<File>{
        val sortingFileName = "Sort.txt"
        val sortingFile = File(File(directoryPath),sortingFileName)
        val files = mutableListOf<File>()

        if (sortingFile.exists()) {
            sortingFile.bufferedReader().forEachLine {
                if (it != "") {
                    val file = File(it)
                    files.add(file)
                }
            }
        }
        return files
    }


    fun normalizeSaveFile(){
        var text = ""
        saveFile.bufferedReader().forEachLine {
            val file = File(it)
            if(file.exists()){
                text += it
                text+="\n"
            }
        }

        saveFile.printWriter().use {
            it.println(text)
        }
    }

    private fun exists(file: File):Boolean{
        if (file.exists())
            return true
        return false
    }

    private fun writeToFile(path: String){
        var text = saveFile.bufferedReader().readText()
        text += path
        saveFile.printWriter().use {
            it.println(text)
        }
    }

    private fun createProgramDir(): File{
        val programDir = File(programDirPath)
        if(!programDir.exists()){
            programDir.mkdirs()
        }
        return programDir
    }

    private fun createSaveFile(): File{
        val saveFile = File(programDir, saveFilePath)
        if(!saveFile.exists()){
            saveFile.createNewFile()
        }
        return saveFile
    }
}