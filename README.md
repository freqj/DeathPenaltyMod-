library TcpCommandsLib;

uses
  SysUtils,
  WinSock;

var
  ClientSocket: TSocket = INVALID_SOCKET;
  WSAData: TWSAData;
  LastError: Integer = 0;

function TcpConnect(Host: PAnsiChar; Port: Integer): Integer; stdcall;
var
  ServerAddr: TSockAddrIn;
  HostEnt: PHostEnt;
  Addr: PAnsiChar;
  Timeout: Integer;
begin
  LastError := 0;
  Result := 0;

  // Инициализация Winsock
  if WSAStartup(MAKEWORD(2, 2), WSAData) <> 0 then
  begin
    LastError := WSAGetLastError();
    Result := -1;
    Exit;
  end;

  // Создание сокета
  ClientSocket := socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
  if ClientSocket = INVALID_SOCKET then
  begin
    LastError := WSAGetLastError();
    WSACleanup();
    Result := -2;
    Exit;
  end;

  // Настройка таймаутов
  Timeout := 5000; // 5 секунд
  setsockopt(ClientSocket, SOL_SOCKET, SO_RCVTIMEO, @Timeout, SizeOf(Timeout));
  setsockopt(ClientSocket, SOL_SOCKET, SO_SNDTIMEO, @Timeout, SizeOf(Timeout));

  // Настройка адреса
  FillChar(ServerAddr, SizeOf(ServerAddr), 0);
  ServerAddr.sin_family := AF_INET;
  ServerAddr.sin_port := htons(Port);
  ServerAddr.sin_addr.S_addr := inet_addr(Host);

  // Если не IP адрес, резолвим hostname
  if ServerAddr.sin_addr.S_addr = INADDR_NONE then
  begin
    HostEnt := gethostbyname(Host);
    if HostEnt = nil then
    begin
      LastError := WSAGetLastError();
      closesocket(ClientSocket);
      ClientSocket := INVALID_SOCKET;
      WSACleanup();
      Result := -3;
      Exit;
    end;
    Addr := HostEnt^.h_addr_list^;
    Move(Addr^, ServerAddr.sin_addr.S_addr, HostEnt^.h_length);
  end;

  // Подключение
  if WinSock.connect(ClientSocket, ServerAddr, SizeOf(ServerAddr)) = SOCKET_ERROR then
  begin
    LastError := WSAGetLastError();
    closesocket(ClientSocket);
    ClientSocket := INVALID_SOCKET;
    WSACleanup();
    Result := -4;
    Exit;
  end;
end;

function TcpSendCommand(Command: PAnsiChar; Length: Integer): Integer; stdcall;
var
  TotalSent, BytesSent: Integer;
begin
  LastError := 0;

  if ClientSocket = INVALID_SOCKET then
  begin
    LastError := -1;
    Result := -1;
    Exit;
  end;

  if (Command = nil) or (Length <= 0) then
  begin
    LastError := -2;
    Result := -2;
    Exit;
  end;

  TotalSent := 0;
  while TotalSent < Length do
  begin
    BytesSent := send(ClientSocket, (Command + TotalSent)^, Length - TotalSent, 0);
    
    if BytesSent = SOCKET_ERROR then
    begin
      LastError := WSAGetLastError();
      Result := -3;
      Exit;
    end;
    
    Inc(TotalSent, BytesSent);
  end;

  Result := TotalSent;
end;

function TcpReceive(Buffer: PAnsiChar; BufferSize: Integer): Integer; stdcall;
var
  BytesReceived: Integer;
begin
  LastError := 0;

  if ClientSocket = INVALID_SOCKET then
  begin
    LastError := -1;
    Result := -1;
    Exit;
  end;

  if (Buffer = nil) or (BufferSize <= 0) then
  begin
    LastError := -2;
    Result := -2;
    Exit;
  end;

  BytesReceived := recv(ClientSocket, Buffer^, BufferSize - 1, 0);
  
  if BytesReceived = SOCKET_ERROR then
  begin
    LastError := WSAGetLastError();
    Result := -3;
    Exit;
  end;

  if BytesReceived > 0 then
    Buffer[BytesReceived] := #0; // Null-terminate

  Result := BytesReceived;
end;

function TcpDisconnect: Integer; stdcall;
begin
  LastError := 0;

  if ClientSocket <> INVALID_SOCKET then
  begin
    shutdown(ClientSocket, SD_BOTH);
    closesocket(ClientSocket);
    ClientSocket := INVALID_SOCKET;
  end;

  WSACleanup();
  Result := 0;
end;

function TcpGetLastError: Integer; stdcall;
begin
  Result := LastError;
end;

exports
  TcpConnect,
  TcpSendCommand,
  TcpReceive,
  TcpDisconnect,
  TcpGetLastError;

begin
end.
