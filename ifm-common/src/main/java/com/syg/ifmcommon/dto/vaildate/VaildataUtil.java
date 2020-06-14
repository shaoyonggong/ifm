package com.syg.ifmcommon.dto.vaildate;



import com.syg.ifmcommon.result.IFMResult;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class VaildataUtil<T> {

    private final static int MaxIndex=10;

    public IFMResult<T> verification(IFMResult<T> ifmResult){

        T t = ifmResult.getBody();
        List<IFMVaildateFieldError> ifrList = ifmResult.getValidationErrors();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        if(t instanceof List){
            int j=0;
            for(int i=0;i<((List) t).size();i++){
                if(j > MaxIndex){
                    break;
                }
                Set<ConstraintViolation<Object>> constraintViolations = validator.validate(((List) t).get(i));
                for(ConstraintViolation constraintViolation:constraintViolations) {
                    IFMVaildateFieldError fieldError=new IFMVaildateFieldError();
                    fieldError.setFieldName(constraintViolation.getPropertyPath().toString());
                    fieldError.setErrorMessage(constraintViolation.getMessage());
                    fieldError.setIndex(i);
                    ifrList.add(fieldError);
                    j++;
                }
            }
        }else{
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
            for(ConstraintViolation constraintViolation:constraintViolations) {
                IFMVaildateFieldError fieldError=new IFMVaildateFieldError(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage(),null);
                ifrList.add(fieldError);
            }
        }
        if(ifrList.size()!=0){
            ifmResult.markRefused();
        }
        return ifmResult;
    }
}
