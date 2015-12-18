Задача:

Необходимо реализовать http-сервер на фреймворке netty
(http://netty.io/), со следующим функционалом:



1. По запросу на http://somedomain/hello отдает «Hello World» через 10 секунд

2. По запросу на http://somedomain/redirect?url=<url> происходит
переадресация на указанный url

3. По запросу на http://somedomain/status выдается статистика:

 - общее количество запросов

 - количество уникальных запросов (по одному на IP)

 - счетчик запросов на каждый IP в виде таблицы с колонкам и IP,
кол-во запросов, время последнего запроса

 - количество переадресаций по url'ам в виде таблицы, с колонками:
url, кол-во переадресаций

 - количество соединений, открытых в данный момент

 - в виде таблицы лог из 16 последних обработанных соединений, колонки
src_ip, URI, timestamp, sent_bytes, received_bytes, speed (bytes/sec)



Все это (вместе с особенностями имплементации в текстовом виде)
выложить на github, приложить к этому:

- скриншоты как выглядят станицы /status в рабочем приложении

- скриншот результата выполнения команды ab – c 100 – n 10000
http://somedomain/status

- еще один скриншот станицы /status, но уже после выполнение команды
ab из предыдущего пункта



Дополнительные требования:

 - любую базу данных использовать запрещено

 - приложение должно собираться Maven'ом

 - все файлы должны быть в UTF8, перенос строки \n



Комментарии:

 - использовать самую последнюю стабильную версию netty в ветке 4.x

 - обратить внимание на многопоточность

 - разобраться в EventLoop’ами netty

Особенности реализации

Для сохранения данных о переадресациях и запросах к серверу используются thread-safe структуры данных из (java.util.concurrent). Для учета траффика и собрания статистики использован класс StatisticsHandler, который наследует класс ChannelTrafficShapingHandler с перегрузкой некоторых методов, используемых для сохранения информации о траффике. Скорость показывается на данный интервал (котоырй равен одной секунде). Если скорость равна 0, значит в последний интервал данные не передавались.

Программа при запуске может принимать один параметр: номер порта, на котором будет стартовать сервер. 

Инструкции по сборке и установке приложения

Сборка приложения осуществляется с помощью Maven. Готовый jar файл уже содержится в папке target. 

Остановка сервера.

Останавливать сервер необходимо при помощи сигнала SIGINT (CTRL+C в Windows и Linux).

Скриншоты лежат в папке screenshots.