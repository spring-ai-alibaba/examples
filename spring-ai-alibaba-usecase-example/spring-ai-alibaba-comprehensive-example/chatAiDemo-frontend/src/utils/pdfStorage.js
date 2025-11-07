// 创建一个新文件来处理 PDF 存储
export const pdfStorage = {
  dbName: 'pdfDB',
  storeName: 'pdfs',
  db: null,
  version: 3,  // 增加版本号

  async init() {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbName, this.version)

      request.onerror = () => reject(request.error)
      request.onsuccess = () => {
        this.db = request.result
        resolve()
      }

      request.onupgradeneeded = (event) => {
        const db = event.target.result
        // 如果存在旧的存储对象，先删除
        if (db.objectStoreNames.contains(this.storeName)) {
          db.deleteObjectStore(this.storeName)
        }
        // 创建新的存储对象，使用简单的键值存储
        db.createObjectStore(this.storeName)
      }
    })
  },

  async savePDF(chatId, pdfFile) {
    if (!this.db) await this.init()
    
    // 将文件转换为 ArrayBuffer
    const arrayBuffer = await pdfFile.arrayBuffer()
    
    return new Promise((resolve, reject) => {
      try {
        const transaction = this.db.transaction([this.storeName], 'readwrite')
        const store = transaction.objectStore(this.storeName)
        const pdfData = {
          data: arrayBuffer,
          name: pdfFile.name,
          type: pdfFile.type,
          timestamp: new Date().getTime()
        }
        // 使用 chatId 作为键
        const request = store.put(pdfData, chatId)

        request.onerror = () => reject(request.error)
        request.onsuccess = () => resolve()
      } catch (error) {
        reject(error)
      }
    })
  },

  async getPDF(chatId) {
    if (!this.db) await this.init()
    return new Promise((resolve, reject) => {
      try {
        const transaction = this.db.transaction([this.storeName], 'readonly')
        const store = transaction.objectStore(this.storeName)
        const request = store.get(chatId)

        request.onerror = () => reject(request.error)
        request.onsuccess = () => {
          const data = request.result
          if (data) {
            // 将 ArrayBuffer 转回 File 对象
            const file = new File([data.data], data.name, {
              type: data.type
            })
            resolve(file)
          } else {
            resolve(null)
          }
        }
      } catch (error) {
        reject(error)
      }
    })
  }
} 
