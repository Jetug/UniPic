package com.example.unipic.models

import java.io.*

class DataSaver {

    private val programDirPath = "/storage/emulated/0/UniPic/"
    private val saveFilePath = "/storage/emulated/0/UniPic/Dirs.txt"
    private val programDir = createProgramDir()
    private val saveFile = createSaveFile()

//    fun saveDir(path: ArrayList<File>){
//        path.forEach{
//            writeToFile(saveFile, it)
//        }
//    }
//
//    @JvmName("saveDir1")
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

    private fun exists(file: File):Boolean{
        if (file.exists())
            return true
        return false
    }

    private fun writeToFile( path: String){
        var text = saveFile.bufferedReader().readText()
        text += path
        saveFile.printWriter().use {
            it.println(text)
        }
    }

    private fun createProgramDir(): File{
        val saveFile = File(programDirPath)
        if(!saveFile.exists()){
            saveFile.mkdirs()
        }
        return saveFile;
    }

    private fun createSaveFile(): File{
        val saveFile = File(saveFilePath)
        if(!saveFile.exists()){
            saveFile.createNewFile()
        }
        return saveFile;
    }
}