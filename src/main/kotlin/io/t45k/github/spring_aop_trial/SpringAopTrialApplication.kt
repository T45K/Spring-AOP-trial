package io.t45k.github.spring_aop_trial

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@SpringBootApplication
class SpringAopTrialApplication

fun main(args: Array<String>) {
    runApplication<SpringAopTrialApplication>(*args)
}

@RestController
class SampleController(private val sampleService: SampleService) {

    @GetMapping("/")
    fun sample(): String {
        val now = LocalDateTime.now()
        val input = SampleInput(now)
        sampleService.sample(input)

        return "hello, world"
    }
}

@Service
class SampleService {
    @SampleAnnotation("now")
    fun sample(input: SampleInput) {
    }
}

data class SampleInput(val now: LocalDateTime) : SampleInterface

interface SampleInterface

annotation class SampleAnnotation(val spel: String)

@Component
@Aspect
class SampleAdvice {
    private val parser = SpelExpressionParser()

    @Before("@annotation(sampleAnnotation)")
    fun exec(joinPoint: JoinPoint, sampleAnnotation: SampleAnnotation) {
        val now = parser.parseExpression(sampleAnnotation.spel)
            .getValue(joinPoint.args[0])
            as LocalDateTime
        println(now)
    }

    @Before("execution(* *(SampleInput, ..)) && args(input, ..)")
    fun exec(joinPoint: JoinPoint, input: SampleInput) {
        val now = input.now
        println(now)
    }
}
