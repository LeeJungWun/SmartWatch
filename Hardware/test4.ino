#include <SPI.h>
#include <Ethernet.h>
#include <UTFT.h>

extern uint8_t BigFont[];         // 글자 폰트
extern uint8_t SevenSegNumFont[]; // 시계숫자 폰트

byte mac[] = { 0x90, 0xA2, 0xDA, 0x0F, 0xD8, 0xC1 }; // 벽시계 이더넷 MAC주소

IPAddress server(192,168,0,9);   // 웹 서버 IP주소
IPAddress ip(192, 168, 0, 5);     // 벽시계 IP주소

EthernetClient client;            // 아두이노 클라이언트 객체

UTFT myGLCD(SSD1963_800ALT,38,39,40,41); // LCD 라이브러리 객체

char all[500];        // 전체 메시지
char cc[2];           // 수신 문자

char* curMsg= NULL;   // 현재 메시지(시간,일정)
char* prevMsg = NULL; // 이전 메시지(시간,일정)

char* token = NULL;   // 파싱 메시지 

int time_x = 370, time_y = 40; // 시간 메시지 X좌표,Y좌표
int sch_y = 250;               // 일정 메시지 Y좌표
int prev_y,cur_y = NULL;

void setup()   // LCD 및 서버 첫 요청       
{
  myGLCD.InitLCD();        // LCD객체 초가화
  myGLCD.setFont(BigFont); // LCD 폰트 설정(큰글씨)
  myGLCD.clrScr();         // LCD 화면 초기화

  // LCD 테두리 설정
  myGLCD.fillScr(VGA_WHITE);      // 흰색 테두리
  myGLCD.setColor(VGA_BLACK);      // 흰색 테두리
  
  myGLCD.fillRoundRect(34,24,764,184);
  myGLCD.fillRoundRect(24,204,774,454);
  
  
  

  Serial.begin(9600);  // 모니터 초기화(Serial)
  while (!Serial)      // 모니터 초기화 대기
  {
    ; 
  }


  if (Ethernet.begin(mac) == 0) // 이더넷 통신 시작
  {
    Serial.println("Failed to configure Ethernet using DHCP");  // 이더넷 통신실패
   
    Ethernet.begin(mac, ip);   // 이더넷 통신 재시작
  }
  Serial.println(Ethernet.localIP()); // 벽시계 IP 모니터 출력
 
 
  delay(1000);                        // 1초 대기
  

  httpRequest();                     // 서버 연결요청


  
}

void loop()
{
  while(client.available())  // 수신 메시지가 있는 경우
  {
    char c = client.read();  // 서버 메시지 수신(문자)
    Serial.print(c);        // 모니터 출력
    sprintf(cc,"%c",c);      // 메시지 저장
    strcat(all,cc);          // 전체 메시지 저장
  } // 메시지 수신 
  
  Serial.println();            // 줄 바꿈 
     
  curMsg = strstr(all,"TODAY"); // 메시지 1차 파싱(현재시간,일정)
   
  if( curMsg != prevMsg )    // 시간 변동할 경우 시간갱신 
  {
    
    prevMsg = curMsg;       // 메시지 저장
    printMessage();         // 메시지 출력
    client.stop();          // 클라이언트 종료
  }
  Serial.println("++++");
  Serial.println(prev_y);           
  Serial.println(cur_y);      
  Serial.println("++++");
  
  
    if( prev_y != cur_y) // 일정 변동 시
    {
      myGLCD.clrScr();   // 화면 초기화
      myGLCD.fillScr(VGA_WHITE);      // 흰색 테두리
      myGLCD.setColor(VGA_BLACK);      // 흰색 테두리
      myGLCD.fillRoundRect(34,24,764,184);
      myGLCD.fillRoundRect(24,214,774,454);
    }
    prev_y = cur_y;        // 
    Serial.println("----");
    Serial.println(prev_y);           
    Serial.println(cur_y);      
    Serial.println("----");
  memset(all,0,sizeof(all)); // 전체메시지 초기화
  httpRequest();             // 서버 연결 재요청
}

void httpRequest()         // 서버연결 요청 
{
  Serial.println();
  Serial.println("Connecting...");  // 연결 중... 메시지 
  
  if (client.connect(server, 80))   // 벽시계 서버 연결 :128,168,0,10 / 80(HTTP) 
  {
    
    Serial.println("Connected");  // 연결 성공 메시지
    Serial.println();             // 모니터 줄 바꿈
    
    // 벽시계 HTTP 요청헤더
    client.println("GET /Calendar/read.php HTTP/1.1");
    client.println("Host: www.arduino.cc");
    client.println("Connection: close");
    client.println();
    
  
    // 벽시계 수신 대기
    while(! client.available()) // 수신 메시지가 없는 경우
    {
      ;
    }   
    
  }
  else // 서버 연결 실패
  {
    Serial.println("Connection failed"); // 연결실패... 메시지
  }
}

void printMessage() // 수신 메시지 LCD 출력 함수
{
    sch_y = 240;    // 일정 메시지 Y좌표 초기화
    
    myGLCD.setColor(VGA_WHITE);      // 흰색 테두리
    myGLCD.setBackColor(VGA_BLACK);      // 흰색 테두리
    
    // 메시지 2차 파싱 후  화면출력
    token = strtok(curMsg,"-");              // "TODAY" 파싱 후 임시 메시지 저장
    myGLCD.print(token,time_x,time_y);       // "TODAY" 출력

    token = strtok(NULL,"-");   // 날짜 파싱
    myGLCD.print(token,time_x-35,time_y+30); // 날짜 출력
    
    myGLCD.setFont(SevenSegNumFont);          // LCD 폰트 설정(시간출력 폰트:세븐 세그먼트)  
    
    token = strtok(NULL,"-");                 // 시간(시) 메시지 파싱
    myGLCD.print(token,time_x-35,time_y+60);  // '시' 출력
    
    token = strtok(NULL,"-");                 // 시간(분) 메시지 파싱
    myGLCD.print(token,time_x+45,time_y+60);  // '분' 출력
    
    myGLCD.setFont(BigFont);                  // LCD 폰트 재설정(큰글씨)
    
    
    while( token != NULL)                     // 일정 메시지가 있을 경우
    {
      token = strtok(NULL,"-");               // 각 일정 메시지 파싱 
      myGLCD.print(token,CENTER,sch_y);       // 일정 출력
      sch_y += 40;                            // 일정 줄 바꿈
    }
    if( sch_y != 240)
    {
      cur_y = sch_y;
    }
    else
    {
      cur_y = prev_y;
    }
    
    Serial.println();
    Serial.println(sch_y);             
    Serial.println();
}
