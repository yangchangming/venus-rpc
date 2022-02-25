package com.anoyi.grpc.annotation;


import org.springframework.context.annotation.Import;
import com.anoyi.grpc.binding.GrpcServiceRegisterScanner;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Inherited
@Retention(RUNTIME)
@Import({GrpcServiceRegisterScanner.class})
public @interface GrpcServiceImpl {
}