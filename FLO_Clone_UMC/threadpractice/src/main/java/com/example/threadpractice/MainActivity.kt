package com.example.threadpractice

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = A()
        val b = B()

        a.start()
        b.start()
        // a와 b가 번갈아서 작업이 진행됨.
        // 이렇게 스레드를 왔다갔다 하는 것을 context switching이라고 함.
        // context switching을 하는 과정에서 약간의 오버헤드(어떤 처리를 하는 데 걸리는 간접적인 처리 시간 및 메모리 비용)가 있을 수 있지만,
        // 정말 빠르게 교체되기 떄문에, 동시에 작업이 수행되는 것처럼 보임
        // 처리 순서를 정하기 위해서는 join이나 AsyncTest를 통해 임의로 지정할 수 있음.
        // a.join()을 호출하면, a 스레드의 작업이 모두 완료된 후에 b 스레드의 작업이 수행됨.
    }

    class A : Thread() {
        override fun run() {
            super.run()
            for (i in 1..100) {
                Log.d("test1", "first: $i")
            }
        }
    }

    class B : Thread() {
        override fun run() {
            super.run()
            for (i in 100 downTo 1) {
                Log.d("test2", "second: $i")
            }
        }
    }


}