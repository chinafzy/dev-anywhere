package me.in1978.tools.anywhere.spring.boot;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(AnywhereDataSourceSetup.class)
public class AnywhereDataSourceAuto {
}
