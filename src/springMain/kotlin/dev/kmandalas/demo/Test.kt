package dev.kmandalas.demo

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.util.*

@Aspect
@Component
class Test {
    @Around("execution(* dev.kmandalas.demo.controller.ProductsController.*(..))")
    fun around(pjp: ProceedingJoinPoint): Any{
        println("I'm around before")
        var a = Any()

        // 執行切入點的方法，obj 為切入點方法執行的結果
        val obj = pjp.proceed()

        println("I'm around after")
        obj?.let {
            a = it
        }
        return a
    }
}

