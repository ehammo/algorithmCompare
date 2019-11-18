table = read.table('C:\\Users\\eduardo\\Documents\\projetos\\algorithmCompare\\creditcard_update.csv', header=TRUE, sep = ",", fill = TRUE)
summary(table)
i = 1
while (i <= 28) {
  hist(table[,i])
  i = i + 1
}