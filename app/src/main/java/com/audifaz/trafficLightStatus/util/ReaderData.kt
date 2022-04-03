package com.audifaz.trafficLightStatus.util

import android.util.Log
import java.lang.StringBuilder

class ReaderData {
    var ad_type: UByte = 0u
    var inited = false
    var scanning //This boolean is used to set the scanning "state" of the textView
            = false

    //with id ScanState
    var got_id = false
    var error = false
    var isAskingToPair = false
    var dev_reset = false
    var app_ver: UByte = 0u

    //        public byte major;
    //        public byte minor;
    //        public byte patch;
    //        public byte board;
    var id_full //ID used to make the search and match in the database
            : Long = 0
    var temp //Temperature
            = 0f
    var country = 0
    var id: Long = 0
    var dev_id //Long because is a hash32 of the mac addr. and we do not want negatives
            : Long = 0
    var mof = 0

    fun PrintBytes(bytes: ByteArray): String? {
        val sb = StringBuilder()
        sb.append("[ ")
        for (b in bytes) {
            sb.append(String.format("0x%02X ", b))
        }
        sb.append("]")
        return sb.toString()
    }

    //In this part we are extracting the data read byte by byte
    fun extractReaderData(data: UByteArray): ReaderData? {
        val rd = ReaderData()
        var ix = 0
        rd.ad_type = data[ix++]
        rd.inited = data[ix].toInt() and 0x01 == 0x01
//        rd.scanning = data[ix].toInt() and 0x02 shl 0 == 0x02 shl 0
        rd.scanning = data[ix].toInt() and 0x02 == 0x02
        rd.got_id = data[ix].toInt() and 0x04 == 0x04
        rd.error = data[ix].toLong() and 0x08L == 0x08L
        rd.isAskingToPair = data[ix].toInt() and 0x40  == 0x40
        rd.dev_reset = data[ix++].toInt() and 0x80 == 0x80
        rd.app_ver = data[ix++]
        //        rd.major = (byte)data[ix++];
//        rd.minor = (byte)data[ix++];
//        rd.patch = (byte)data[ix++];
//        rd.board = (byte)data[ix++];
        rd.dev_id = ((data[ix++].toLong() and 0xffL shl 0) or
                (data[ix++].toLong() and 0xffL shl 8)) // |
        //                ((data[ix++] & 0xffL) << 16) |
//                ((data[ix++] & 0xffL) << 24) ;
        rd.id_full = (data[ix++].toLong() and 0xffL shl 0) or
                (data[ix++].toLong() and 0xffL shl 8) or
                (data[ix++].toLong() and 0xffL shl 16) or
                (data[ix++].toLong() and 0xffL shl 24) or
                (data[ix++].toLong() and 0xffL shl 32) or
                (data[ix++].toLong() and 0xffL shl 40) or
                (data[ix++].toLong() and 0xffL shl 48) or
                (data[ix++].toLong() and 0xffL shl 56)

        val tmp: Int = (data[ix++].toInt() and 0xff shl 0) or
                (data[ix++].toInt() and 0xff shl 8)

        rd.mof = data[ix++].toInt() and 0xff shl 0 or
                (data[ix++].toInt() and 0xff shl 8)
        rd.temp = tmp.toFloat() / 10
        rd.country = (rd.id_full / 1000000000000L).toInt()
        rd.id = rd.id_full % 1000000000000L
        return rd
    }

    //Simple print to the console, to check parameters advertised to the smartphone
    fun logParameters(rd: ReaderData?){
        Log.i("ClassReaderTest", "Ad Type: ${rd?.ad_type}")
        Log.i("ClassReaderTest", "App Ver: ${rd?.app_ver}")
        Log.i("ClassReaderTest", "DeviceId: ${rd?.dev_id}")
        Log.i("ClassReaderTest", "Id Full: ${rd?.id_full}")
        Log.i("ClassReaderTest", "Temperature: ${rd?.temp}")
        Log.i("ClassReaderTest", "MOF: ${rd?.mof}")
        Log.i("ClassReaderTest", "Country: ${rd?.country}")
        Log.i("ClassReaderTest", "DeviceId: ${rd?.id}")
    }


    fun returnIsAskingToPair(rd: ReaderData?): Boolean {
        return rd!!.isAskingToPair
    }

    fun returnIsScanning(rd: ReaderData?): Boolean{
        return rd!!.scanning
    }

    fun returnGotId(rd: ReaderData?): Boolean{
        return rd!!.got_id
    }

}