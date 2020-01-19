# Distribuerade beräkningar med JPPF

Tips för att labba med [JPPF](https://www.jppf.org) på Mejslas vässardag 2019-01-23.


## Komma igång med labben

Bygg projektet:

```
mvn package
```


### Testa lokalt

För att testa lokalt behöver du inte ändra något. Det räcker med att starta alla delar av systemet: en server, en eller flera noder och adminklienten. Sen kan du köra testklienten och se vad som händer.


### Anslut till labbklustret

När du ansluter din dator till labbklustret behöver du ingen server. Du måste däremot ändra några inställningar för att noder och klienter ska hitta den gemensamma servern. Alla noder och klienter i ett kluster måste använda samma version av Java (11) och serialisering (DefaultJavaSerialization).


#### node/config/jppf.properties

```
jppf.server.host=<den gemensamma serverns IP-adress>
jppf.discovery.enabled=false
```

Eventuellt vill du ändra hur många trådar en nod får lägga rabarber på:

```
jppf.processing.threads=<antal trådar>
```


#### node/config/jppf.properties och admin/config/jppf.properties

```
driver1.jppf.server.host=<den gemensamma serverns IP-adress>
jppf.discovery.enabled=false
```

Nu kan du starta en eller flera noder och köra testklienten igen.


## Labba

För att enkelt att komma igång kan du utgå från klassen `TemplateApplicationRunner`, som till största delen är kopierad från JPPFs exempelkod. I `main` kan man se tre olika sätt att köra distribuerade jobb med JPPF. Den som föredrar enhetstester kan istället utgå från `SimpleTaskIT`.

Det finns också några exempel-tasks att testa med.


### Förslag på saker att labba med

Du har säkert egna saker du vill testa, men annars finns här är en lista med idéer:

* Gör JPPFs egen [tutorial](https://www.jppf.org/doc/6.1/index.php?title=A_first_taste_of_JPPF)
* Testa failover genom att stoppa en nod som exekverar ett jobb och se om en annan nod tar över
* Skriv en klient som gör något som tar lång tid, som t.ex. hitta primtal, dela upp beräkningen i flera delar, kör beräkningen distribuerat och sätt ihop resultatet efteråt
* Byt [serialisering](https://www.jppf.org/doc/6.1/index.php?title=Specifying_alternate_serialization_schemes) till något annat än Javas inbyggda ([exempel](https://www.jppf.org/samples-pack/KryoSerializer/))
* Testa en färdig klient, t.ex. [Mandelbrot-fraktaler](https://github.com/dykstrom/jppf-mandel) eller något av [JPPFs exempel](https://www.jppf.org/samples-pack/index.php)
* Kör en nod med en SecurityManager installerad
* Skapa jobb med [SLA](https://www.jppf.org/doc/6.1/index.php?title=Job_Service_Level_Agreement), t.ex. jobb får bara köras på macOS, eller på en nod med minst fyra trådar
* Skriv en egen [lastbalanserare](https://www.jppf.org/doc/6.1/index.php?title=Creating_a_custom_load-balancer) ([exempel](https://www.jppf.org/samples-pack/CustomLoadBalancer))
* JPPF i molnet ([JPPF deployment](https://www.jppf.org/doc/6.1/index.php?title=JPPF_Deployment), [Docker](https://hub.docker.com/u/jppfgrid), [Kubernetes](https://github.com/jppf-grid/JPPF/tree/master/containers/k8s/jppf))
* Skriv en bitcoin-brytare, kör på alla andras datorer och bli rik!
* Eller något roligare som du kommer på själv


## JPPF-resurser

* [Startsida](https://www.jppf.org)
* [Dokumentation](https://www.jppf.org/doc/6.1/index.php?title=Main_Page)
* [Tutorial](https://www.jppf.org/doc/6.2/index.php?title=A_first_taste_of_JPPF)
