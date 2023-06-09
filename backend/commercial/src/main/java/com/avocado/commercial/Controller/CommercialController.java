package com.avocado.commercial.Controller;

import com.avocado.commercial.Dto.request.CommercialCancelReqDto;
import com.avocado.commercial.Dto.request.CommercialReqDto;
import com.avocado.commercial.Dto.response.Analysis;
import com.avocado.commercial.Dto.response.RegistedCommercial;
import com.avocado.commercial.Entity.Commercial;
import com.avocado.commercial.Service.CommercialService;
import com.avocado.commercial.Dto.response.CommercialRespDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommercialController {

    private CommercialService commercialService;

    @Autowired
    public CommercialController(CommercialService commercialService){
        this.commercialService = commercialService;
    }

    // 70대까지
    // 다양한 조건에서 되게 만들어야 함 (ex. personalColor가 없음)
    // 팝업 광고 1개, 캐러셀 광고 5개 합쳐서 보내자
    @GetMapping("/ads")
    public ResponseEntity<CommercialRespDto> exposeCommercial(
            @RequestParam(name = "mbti_id", defaultValue = "-1") int mbtiId, @RequestParam(name = "age", defaultValue = "-1") int age,
            @RequestParam(name = "personal_color_id", defaultValue = "-1") int personalColorId, @RequestParam(name = "gender", defaultValue = "X") char gender){
        CommercialRespDto commercialRespDto = commercialService.getCommercialExposure(mbtiId, age, personalColorId, gender);
        
        // 응답 환경 구성 필요
        return ResponseEntity.ok().header("msg","success").body(commercialRespDto);
//                <CommercialRespDto>(commercialRespDto, HttpStatus.OK);
    }
    @GetMapping("/analyses/{commercial_id}")
    public ResponseEntity<List<Analysis>> getAnalyses(@PathVariable("commercial_id") int commercialId){
//        List<Analysis> analysisList = commercialService.getAnalyses(commercialId);
        List<Analysis> analysisList = commercialService.getCommercialStatistic(commercialId);
        return new ResponseEntity<List<Analysis>>(analysisList,HttpStatus.OK);
    }


    @PostMapping( "/ads")
    public ResponseEntity<Map<String,String>> registCommercial(CommercialReqDto commercial, HttpServletRequest request){
        Map<String,String> msg = new HashMap<String,String>();
        msg.put("msg","success");
        commercialService.saveCommercial(commercial, request);
        return new ResponseEntity<Map<String,String>>(msg,HttpStatus.CREATED);
    }

    @GetMapping("/ads/registed")
    public ResponseEntity<List<Commercial>> getRegistedCommercial(HttpServletRequest request){
        List<Commercial> list = commercialService.getRegistedCommercial(request);

        return new ResponseEntity<List<Commercial>>(list,HttpStatus.OK);
    }

    @DeleteMapping("/ads")
    public ResponseEntity<Map<String,String>> cancelCommercial(@RequestBody CommercialCancelReqDto commercialCancelReqDto, HttpServletRequest request){

        commercialService.removeCommercial(commercialCancelReqDto,request);
        Map<String,String> msg = new HashMap<String,String>();
        msg.put("msg","success");
        return new ResponseEntity<Map<String,String>>(msg,HttpStatus.ACCEPTED);
    }


}
