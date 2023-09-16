# Weather Now BE
날씨예보 서비스 제작 토이프로젝트 Weather Now의 백엔드 프로젝트입니다.
Spring Boot를 기반으로 REST API 패턴으로 서버를 구축했습니다. 
OpenAPI 사용을 목표로 구상한 토이프로젝트로 기상청 단기예보, 중기예보 API를 사용했습니다. 

# 전체 흐름
- API 호출 -> 기상청 API 호출 -> 기상청 API 문서에 의거한 데이터 변환(비즈니스 로직) -> DTO -> 데이터 반환(JSON)

# 기능
1) 현재 날씨 가져오기
2) 단기 예보 가져오기
3) 중기 예보 가져오기(예정)

# Note
- DB 없음 (향후 중기, 장기 예보 시 안정성을 고려하여 추가할 수 있음)
- FE 레포지토리: https://github.com/SeungUkAhn/weatherNow-front-end
