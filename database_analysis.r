table = read.table('C:\\Users\\eduardo\\Downloads\\creditcard.csv', header=TRUE, sep = ",", fill = TRUE)
library(dplyr)
size_total=length(table[,1])
table <- table %>% mutate(ID= c(1:size_total))
table_sim <- table %>% filter(Class == "1")
table_nao <- table %>% filter(Class == "0")
size_sim = length(table_sim$V1)
ID <- sample(table_nao$ID, size=size_sim, replace = T)
amostra <- as.data.frame(ID)
table_nao <- left_join(amostra, table, by = "ID")
table2 <- rbind(table_nao, table_sim)

table3 = table2
table3$Class <- gsub("0", "Nao", table3$Class)
table3$Class <- gsub("1", "Sim", table3$Class)


write.csv(table3, "undersampling.csv")





table = read.table('C:\\Users\\eduardo\\Documents\\projetos\\algorithmCompare\\all.csv', header=FALSE, sep = ",", fill = TRUE)
library(nortest)
i = 1
while (i < length(table$V8)) {
  naive_bayes = c()
  K_vizinhos = c()
  Multilayer_perceptron = c()
  Multilayer_perceptron_3 = c()
  Support_Vector_machine_poly = c()
  Support_Vector_machine_radio = c()
  
  j = 1
  while (i <= 10) {
    naive_bayes[j] = table[i,8]
    i = i + 1
    j = j + 1
  }
  j = 1
  while (i <= 20) {
    K_vizinhos[j] = table[i,8]
    i = i + 1
    j = j + 1
  }
  j = 1
  while (i <= 30) {
    Multilayer_perceptron[j] = table[i,8]
    i = i + 1
    j = j + 1
  }
  j = 1
  while (i <= 40) {
    Multilayer_perceptron_3[j] = table[i,8]
    i = i + 1
    j = j + 1
  }
  j = 1 
  while (i <= 50) {
    Support_Vector_machine_poly[j] = table[i,8]
    i = i + 1
    j = j + 1
  }
  j = 1
  while (i <= 60) {
    Support_Vector_machine_radio[j] = table[i,8]
    i = i + 1
    j = j + 1
  }
}

hist(naive_bayes)
hist(K_vizinhos)
hist(Multilayer_perceptron)
hist(Multilayer_perceptron_3)
hist(Support_Vector_machine_poly)
hist(Support_Vector_machine_radio)

normalityTest(naive_bayes)
normalityTest(K_vizinhos)
normalityTest(Multilayer_perceptron)
normalityTest(Multilayer_perceptron_3)
normalityTest(Support_Vector_machine_poly)
normalityTest(Support_Vector_machine_radio)

normalityTest <- function (dados) {  
  t1 <- ks.test(dados, "pnorm") # KS
  t2 <- lillie.test(dados) # Lilliefors
  t3 <- cvm.test(dados) # Cramér-von Mises
  t4 <- shapiro.test(dados) # Shapiro-Wilk
  t5 <- sf.test(dados) # Shapiro-Francia
  t6 <- ad.test(dados) # Anderson-Darling
  # Tabela de resultados
  testes <- c(t1$method, t2$method, t3$method,
              t6$method)
  estt <- as.numeric(c(t1$statistic, t2$statistic, t3$statistic,
                        t6$statistic))
  valorp <- c(t1$p.value, t2$p.value, t3$p.value, 
              t6$p.value)
  resultados <- cbind(estt, valorp)
  rownames(resultados) <- testes
  colnames(resultados) <- c("Estatística", "p")
  print(resultados, digits = 4)
  
  i = i + 1
}



#data.table - fread
#caret
size_total=length(table[,1])
table <- table %>% mutate(ID= c(1:size_total))
table_sim <- table %>% filter(Class == "Sim")
table_nao <- table %>% filter(Class == "Nao")
size_nao = length(table_nao$V1)
ID <- sample(table_sim$ID, size=size_nao, replace = T)
amostra <- as.data.frame(ID)
table2 <- left_join(amostra, table, by = "ID")

final_table = as.data.frame(rbind(table_nao, table2))

all = read.table('C:\\Users\\eduardo\\Documents\\projetos\\algorithmCompare\\all.csv', header=FALSE, sep = ",", fill = TRUE)

i = 1
j = 1
resposta = matrix(0,nrow = 10, ncol = 6)
colnames(resposta) = c("IBK", "MLP1", "MLP3", "NAIVE", "SMOP", "SMOR")
currentAlgData = c()
currentAlgData[1] = all[1,8]
currentAlgData[2] = all[2,8]
currentAlgData[3] = all[3,8]
currentAlgData[4] = all[4,8]
currentAlgData[5] = all[5,8]
currentAlgData[6] = all[6,8]
currentAlgData[7] = all[7,8]
currentAlgData[8] = all[8,8]
currentAlgData[9] = all[9,8]
currentAlgData[10] = all[10,8]
resposta[,1] = currentAlgData
currentAlgData[1] = all[11,8]
currentAlgData[2] = all[12,8]
currentAlgData[3] = all[13,8]
currentAlgData[4] = all[14,8]
currentAlgData[5] = all[15,8]
currentAlgData[6] = all[16,8]
currentAlgData[7] = all[17,8]
currentAlgData[8] = all[18,8]
currentAlgData[9] = all[19,8]
currentAlgData[10] = all[20,8]
resposta[,2] = currentAlgData
currentAlgData[1] = all[21,8]
currentAlgData[2] = all[22,8]
currentAlgData[3] = all[23,8]
currentAlgData[4] = all[24,8]
currentAlgData[5] = all[25,8]
currentAlgData[6] = all[26,8]
currentAlgData[7] = all[27,8]
currentAlgData[8] = all[28,8]
currentAlgData[9] = all[29,8]
currentAlgData[10] = all[30,8]
resposta[,3] = currentAlgData
currentAlgData[1] = all[31,8]
currentAlgData[2] = all[32,8]
currentAlgData[3] = all[33,8]
currentAlgData[4] = all[34,8]
currentAlgData[5] = all[35,8]
currentAlgData[6] = all[36,8]
currentAlgData[7] = all[37,8]
currentAlgData[8] = all[38,8]
currentAlgData[9] = all[39,8]
currentAlgData[10] = all[40,8]
resposta[,4] = currentAlgData
currentAlgData[1] = all[41,8]
currentAlgData[2] = all[42,8]
currentAlgData[3] = all[43,8]
currentAlgData[4] = all[44,8]
currentAlgData[5] = all[45,8]
currentAlgData[6] = all[46,8]
currentAlgData[7] = all[47,8]
currentAlgData[8] = all[48,8]
currentAlgData[9] = all[49,8]
currentAlgData[10] = all[50,8]
resposta[,5] = currentAlgData
currentAlgData[1] = all[51,8]
currentAlgData[2] = all[52,8]
currentAlgData[3] = all[53,8]
currentAlgData[4] = all[54,8]
currentAlgData[5] = all[55,8]
currentAlgData[6] = all[56,8]
currentAlgData[7] = all[57,8]
currentAlgData[8] = all[58,8]
currentAlgData[9] = all[59,8]
currentAlgData[10] = all[60,8]
resposta[,6] = currentAlgData

resposta
fresult = friedman.test(resposta)


resposta = all[,8]
tratamento = all[,1]
bloco = rep(1:10,60)


library(agricolae)
comparison<-friedman(bloco,tratamento,resposta,alpha=0.05, group=TRUE,main="Teste de Friedman")


